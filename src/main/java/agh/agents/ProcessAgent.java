package agh.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.Arrays;

public class ProcessAgent extends Agent {

    private String[] processParameters = new String[9];
    private int classifier;

    protected void setup() {
        Object[] args = getArguments();

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage reply;

                ArrayList<MessageTemplate> templates = new ArrayList<>();
                templates.add(MessageTemplate.MatchPerformative(AgentMessages.CHECK_AGENT));
                templates.add(MessageTemplate.MatchPerformative(AgentMessages.START_PROCESS));
                templates.add(MessageTemplate.MatchPerformative(AgentMessages.SET_PROCESS_VALUES));

                ACLMessage[] checkMsg = new ACLMessage[templates.size()];
                int counter = 0;
                for (MessageTemplate checkState : templates) {
                    checkMsg[counter++] = receive(checkState);
                }
                for (ACLMessage msg : checkMsg) {
                    if (msg != null) {
                        switch (msg.getPerformative()) {
                            case (AgentMessages.CHECK_AGENT):
                                reply = new ACLMessage(AgentMessages.CHECK_AGENT);
                                reply.setContent("success");
                                reply.addReceiver(new AID(args[0].toString(), AID.ISLOCALNAME));
                                send(reply);
                                break;
                            case (AgentMessages.SET_PROCESS_VALUES):
                                String[] params = msg.getContent().split("]");
                                processParameters[0] = params[0];
                                String[] arr = params[1].split(" ");
                                String[] a = Arrays.copyOfRange(arr, 1, arr.length);
                                classifier = Integer.parseInt(a[0]);
                                reply = new ACLMessage(AgentMessages.SET_PROCESS_VALUES_ACK);
                                reply.setContent("success");
                                reply.addReceiver(new AID(args[0].toString(), AID.ISLOCALNAME));
                                send(reply);
                                break;
                            case (AgentMessages.START_PROCESS):
                                String result;
                                String[] parameters = fromString(processParameters[0]);
                                ProductionProcess process = new ProductionProcess(parameters, classifier); //instancja processu z zainicjalizowanymi docelowymi parametrami
                                String[] wjp = process.runProcess();
                                result = wjp[0] + "-" + wjp[1] + "-" + wjp[2];
                                reply = new ACLMessage(AgentMessages.RECEIVE_RESULT);
                                reply.addReceiver(new AID(args[0].toString(), AID.ISLOCALNAME));
                                reply.setContent(result);
                                send(reply);
                                doDelete();
                        }
                    }
                }

                block();
            }
        });
    }

    private String[] fromString(String string) {
        String[] strings = string.replace("[", "").replace("]", "").split(", ");
        String result[] = new String[strings.length];
        System.arraycopy(strings, 0, result, 0, result.length);
        return result;
    }
}
