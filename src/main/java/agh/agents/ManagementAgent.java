package agh.agents;

import agh.CPUUtils;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ManagementAgent extends Agent {


    public static ArrayList<AID> dcspAgents = new ArrayList<>();
    public static Map<String, Integer> agentsMap = new HashMap<>();

    private Map<String, String> bestAgentView;
    private boolean learnModuleStarted;

    private String[] agentParametersOrder = new String[] {
            "Skład",
            "WariantObróbki",
            "TemperaturaAustenityzowania",
            "TemperaturaAusferrytyzowania",
            "CzasAusferrytyzacji"
    };


    private AID createDcspAgent(boolean costAgent) {
        ContainerController containerController = getContainerController();

        String processNickname = "Dcsp-agent-" + (costAgent ? "Cost" : dcspAgents.size());
        Object[] args = new Object[2];
        args[0] = getLocalName();
        args[1] = dcspAgents.size();

        try {
            AgentController process = containerController.createNewAgent(processNickname,
                    "agh.agents.DcspAgent", args);
            process.start();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new AID(processNickname, AID.ISLOCALNAME);
    }

    protected void setup() {

        //initialize UI agent
        try {
            Object[] args = new Object[1];
            args[0] = getLocalName();
            ContainerController cc = getContainerController();

            AgentController ui = cc.createNewAgent("UI-agent",
                    "agh.agents.InterfaceAgent", args);
            ui.start();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        DataSetManager.Init();

        System.out.println("PA: UIAgent initialized");

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {

                if (DataSetManager.subsystemTypeId == 144 || DataSetManager.subsystemTypeId == 3){
                    manageDcspAction();
                }

                if (DataSetManager.subsystemTypeId == 1 || DataSetManager.subsystemTypeId == 3){
                    manageLearningAction();
                }

                CPUUtils.sleep(100);
            }
        });

    }

    private void manageLearningAction() {

        if (learnModuleStarted)
            return;
        learnModuleStarted = true;

        Object[] args = new Object[1];
        args[0] = getLocalName();

        try {

            ContainerController containerController = getContainerController();
            AgentController process = containerController.createNewAgent("Learning-agent",
                    "agh.agents.LearningAgent", args);
            process.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }


    }

    private void manageDcspAction() {


        ACLMessage srcMsg = receive();
        if (srcMsg != null) {
            switch (srcMsg.getPerformative()) {
                case (ACLMessage.PROPOSE):

                    Map<String, List<String>> dataSourceParameters = DataSetManager.getParameters(DataSetManager.dataSource);

                    createDcspAgents(dataSourceParameters.size());
                    //CPUUtils.sleep(500);

                    assignParametersToAgents(srcMsg, dataSourceParameters);

                    createConstraints(srcMsg);

                    sendOk(srcMsg);

                    waitForEnd();
                    sendGetAgentView(srcMsg);

                    break;
                case (ACLMessage.INFORM):

                    Map<String, String> agentView = getAgentViewFromMessage(srcMsg);

                    int cost = DataSetManager.getCost(agentView);

                    //      System.out.println("XCost: " + MyDataSet.maxCost + " " + cost);
                    //System.out.println("Last iteration cost: " + cost);
                    if (bestAgentView != null && cost >= DataSetManager.maxCost) {

                        //          System.out.println("Found best solution: ");
                        //          System.out.println("Cost: " + MyDataSet.maxCost);
                        System.out.println(DataSetManager.minQuality + "\t" + DataSetManager.getQuality(DataSetManager.dataSource, bestAgentView) + "\t"
                                + DataSetManager.maxCost + "\t" + DcspAgent.noG + "\t" + DcspAgent.Iteration);
                        //    System.out.println(MyDataSet.getQuality(DataSetManager.dataSource, bestAgentView));

                        String r = DataSetManager.getQuality(DataSetManager.dataSource, bestAgentView) + "\t";
                        for (Map.Entry<String, String> entry : bestAgentView.entrySet()) {
                            r += ", " + entry.getKey() + " = " + entry.getValue();
                            // System.out.println(entry.getKey() + " = " + entry.getValue());
                        }
                        System.out.println(r);
                        if (mQuality < DataSetManager.maxQuality) {
                            //CPUUtils.sleep(1000);
                            DataSetManager.minQuality = ++mQuality;
                            DataSetManager.maxCost = -1;
                            DcspAgent.noG = 0;
                            bestAgentView = null;
                            agentsMap.clear();

                            //    System.out.println();
                            //    System.out.println("Min Quality: " + MyDataSet.minQuality);

                            ACLMessage m = new ACLMessage(ACLMessage.REQUEST);
                            for (AID aid : dcspAgents)
                                m.addReceiver(aid);
                            m.setContent("clear");
                            send(m);

                            m = new ACLMessage(ACLMessage.PROPOSE);
                            m.setContent("");
                            m.addReceiver((AID) srcMsg.getAllReceiver().next());

                            send(m);
                        }
                        return;
                    }

                    DataSetManager.maxCost = cost;
                    bestAgentView = agentView;

                    sendOk(srcMsg);

                    waitForEnd();
                    sendGetAgentView(srcMsg);

                    break;
            }
        }
    }


    private double mQuality = DataSetManager.maxQuality;

    private Map<String, String> getAgentViewFromMessage(ACLMessage srcMsg) {
        Map<String, String> agentView = new HashMap<>();
        CPUUtils.sleep(100); //TODO: bug
        for (String param : srcMsg.getContent().split(" ")[1].split(",")) {
            agentView.put(param.split("=")[0], param.split("=")[1]);
        }

        return agentView;
    }

    private void sendGetAgentView(ACLMessage srcMsg) {
        ACLMessage msg;
        msg = srcMsg.createReply();
        msg.clearAllReceiver();
        msg.addReceiver(dcspAgents.get(dcspAgents.size() - 1));
        msg.setContent("getAgentView");
        send(msg);
    }

    private void waitForEnd() {
        int prevIteration = -1;
        while (DcspAgent.Iteration != prevIteration) {
            prevIteration = DcspAgent.Iteration;

            CPUUtils.sleep(100);
        }

        //System.out.println("Iterations count: " + prevIteration);
    }

    private void sendOk(ACLMessage srcMsg) {
        ACLMessage msg;
        msg = srcMsg.createReply();
        msg.setPerformative(ACLMessage.REQUEST);
        msg.clearAllReceiver();
        for (AID aid : dcspAgents)
            msg.addReceiver(aid);
        msg.setContent("ok?");
        send(msg);
    }

    private void createConstraints(ACLMessage srcMsg) {
        ACLMessage msg;
        List<Map<String, String>> constraints = DataSetManager.getConstraints(DataSetManager.dataSource);
        for (Map<String, String> constraint : constraints) {

            msg = srcMsg.createReply();
            msg.setPerformative(ACLMessage.INFORM);
            msg.clearAllReceiver();

            List<String> constraintStrings = new ArrayList<>();

            for (Map.Entry<String, String> constraintParameter : constraint.entrySet()) {
                msg.addReceiver(dcspAgents.get(agentsMap.get(constraintParameter.getKey())));
                constraintStrings.add(constraintParameter.getKey() + "=" + constraintParameter.getValue());
            }

            msg.setContent("constraint " + String.join(",", constraintStrings));
            send(msg);
        }
    }

    private void assignParametersToAgents(ACLMessage check, Map<String, List<String>> agentParameters) {
        ACLMessage msg;
        for (String agentParameter : agentParametersOrder) {

            msg = check.createReply();
            msg.setPerformative(ACLMessage.INFORM);
            msg.clearAllReceiver();
            msg.addReceiver(dcspAgents.get(agentsMap.size()));
            msg.setContent("param " + agentParameter + " " + String.join(",", agentParameters.get(agentParameter)));
            send(msg);

            agentsMap.put(agentParameter, agentsMap.size());
        }
    }

    private void createDcspAgents(int count) {
        if (dcspAgents.size() != 0)
            return;

        for (int i = 0; i < count; i++) {
            dcspAgents.add(createDcspAgent(false));
        }
        dcspAgents.add(createDcspAgent(true));
    }
}
