package agh.agents;

import agh.CPUUtils;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.*;
import java.util.stream.Collectors;

public class DcspAgent extends Agent {

    String parameterName;
    List<String> parameterValues;
    String currentValue;
    Map<String, String> agentView = new HashMap<>();

    String sentOk;

    int agentId;
    List<Constraint> _constraints = new ArrayList<>();
    List<Integer> _connectedAgentIds = new ArrayList<>();
    List<Map<String, String>> nogoodList = new ArrayList<>();

    public static int Iteration;

    protected void setup() {
        Object[] args = getArguments();
        agentId = Integer.valueOf(args[1].toString());
        if (agentId < 5)
            _connectedAgentIds.add(5);

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {

                ACLMessage msg = receive();
                if (msg != null) {
                    log("got: " + msg.getContent());

                    // param Skład W1,W2,W3
                    if (msg.getContent().startsWith("param"))
                        handleParamMessage(msg);

                    // ok? Czas krótki
                    if (msg.getContent().startsWith("ok?"))
                        handleOkMessage(msg);

                    // nogood WariantObróbki=standardowa,TemperaturaAustenityzowania=niska
                    if (msg.getContent().startsWith("nogood"))
                        handleNogoodMessage(msg);

                    // constraint Skład=W1,WariantObróbki=standardowa
                    if (msg.getContent().startsWith("constraint"))
                        handleConstraintMessage(msg);

                    // getAgentView
                    if (msg.getContent().startsWith("getAgentView"))
                        handleGetAgentView(msg);

                    // getAgentView
                    if (msg.getContent().startsWith("clear")){
                        _constraints.clear();
                        nogoodList.clear();
                        agentView.clear();
                        currentValue = null;
                        _connectedAgentIds.clear();
                        if (agentId < 5)
                            _connectedAgentIds.add(5);
                    }
                }

                //block();
                CPUUtils.sleep(10);
            }
        });
    }

    private void handleParamMessage(ACLMessage msg) {
        String[] split = msg.getContent().split(" ");
        parameterName = split[1];
        parameterValues = Arrays.asList(split[2].split(","));
    }

    private void handleOkMessage(ACLMessage msg) {
        Iteration++;
        String[] split = msg.getContent().split(" ");
        if (split.length > 1) {
            if (agentView.containsKey(split[1]))
                agentView.replace(split[1], split[2]);
            else
                agentView.put(split[1], split[2]);
        }
        checkAgentView(msg);
    }
public static int noG;
    private void handleNogoodMessage(ACLMessage msg) {
        sentOk = null;
noG++;
        String[] split = msg.getContent().split(" ")[1].split(",");

        Map<String, String> map = new HashMap<>();

        for (String s : split) {
            String[] split2 = s.split("=");

            map.put(split2[0], split2[1]);

            agentView.put(split2[0], split2[1]);
        }

        if (nogoodList.contains(map))
            return;

        nogoodList.add(map);

        checkAgentView(msg);

    }

    private void handleConstraintMessage(ACLMessage msg) {
        Map<String, String> map = new HashMap<String, String>();
        for (String constr : msg.getContent().split(" ")[1].split(",")) {

            String parameterName2 = constr.split("=")[0];
            String parameterValue2 = constr.split("=")[1];
            map.put(parameterName2, parameterValue2);

            if (parameterName != parameterName2 && ManagementAgent.agentsMap.get(parameterName2) > agentId
                && !_connectedAgentIds.contains(ManagementAgent.agentsMap.get(parameterName2))) {

                _connectedAgentIds.add(ManagementAgent.agentsMap.get(parameterName2));

                log("added link to agent " + parameterName2);
            }
        }

        if (!map.entrySet().stream().anyMatch(a -> ManagementAgent.agentsMap.get(a.getKey()) > agentId)) {
            _constraints.add(new Constraint(map));


            log("added constraint");
        }
    }


    private void checkAgentView(ACLMessage parent) {

        if (agentId == 5) {

            int cost = DataSetManager.getCost(agentView);
            log("cost: " + cost);
            if (DataSetManager.maxCost != -1 && cost >= DataSetManager.maxCost)
                backtrack(parent);

        } else {

            currentValue = null;

            for (String parameterValue : parameterValues) {

                Map<String, String> map = new HashMap<>(agentView);
                map.put(parameterName, parameterValue);

                if (nogoodList.contains(map))
                    continue;

                //nogoodList
                //createAgentViewString() + "," + parameterName + "="

                if (_constraints.stream().allMatch(c -> c.IsGood(map))) {
                    currentValue = parameterValue;
                    break;
                }
            }

            if (currentValue == null) {
                sentOk = null;
                backtrack(parent);
                return;
            }

            if (sentOk == currentValue)
                return;

            //ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
            ACLMessage msg = parent.createReply();
            msg.clearAllReceiver();

            for (int agentId : _connectedAgentIds)
                msg.addReceiver(ManagementAgent.dcspAgents.get(agentId));

            msg.setContent("ok? " + parameterName + " " + currentValue);
            msg.setPerformative(ACLMessage.PROPOSE);
            send(msg);
            sentOk = currentValue;

            return;
            //}
        }
    }

    private void backtrack(ACLMessage parent) {

        if (agentId == 0)
            return;

        //ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
        ACLMessage msg = parent.createReply();
        msg.clearAllReceiver();
        msg.addReceiver(ManagementAgent.dcspAgents.get(agentId - 1));


        msg.setPerformative(ACLMessage.REJECT_PROPOSAL);
        msg.setContent("nogood " + createAgentViewString());
        send(msg);

        log("send: nogood");
    }

    private String createAgentViewString() {

        List<String> list = new ArrayList<>();
        for (Map.Entry<String, String> entrySet : agentView.entrySet()) {
            if (!entrySet.getKey().equals(parameterName))
                list.add(entrySet.getKey() + "=" + entrySet.getValue());
        }

        return String.join(",", list);
    }

    private void handleGetAgentView(ACLMessage parent) {

        List<String> entries = agentView.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.toList());

        ACLMessage msg = parent.createReply();
        msg.setPerformative(ACLMessage.INFORM);
        msg.setContent("agentView " + String.join(",", entries));
        send(msg);

        log("send: agentView");
    }

    private void log(String msg) {
        //System.out.println("Agent " + parameterName + " (" + agentId + ") " + msg);
    }
}
