package agh.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.Arrays;

public class UIAgent extends Agent implements InterfaceUI {

    private Object[] args;
    private int runProcessStep = 0;
    private int learningStep = 0;
    private int DBQueryStartStep = 0;
    private String agentResult;
    private boolean runProcessFinished = false;
    private boolean learningFinished = false;
    private boolean dbQueryFinished = false;

    public UIAgent() {
        registerO2AInterface(InterfaceUI.class, this);
    }

    public String runProcess(String[] data, int classifier) {
        agentResult = null;
        addBehaviour(new Behaviour() {
            @Override
            public void action() {
                String msgContent = Arrays.toString(data) + " " +
                        Integer.toString(classifier);
                MessageTemplate msgTmp;
                ACLMessage msgReceive;
                switch (runProcessStep) {

                    case (0):
                        ACLMessage msgProcessInit = new ACLMessage(AgentMessages.START_PROCESS_AGENT);
                        msgProcessInit.setContent("");
                        msgProcessInit.addReceiver(new AID(args[0].toString(), AID.ISLOCALNAME));
                        send(msgProcessInit);
                        runProcessStep = 1;
                        block();

                    case (1):
                        msgTmp = MessageTemplate.MatchPerformative(AgentMessages.START_PROCESS_AGENT_ACK);
                        msgReceive = receive(msgTmp);
                        if (msgReceive != null) {
                            runProcessStep = 2;
                        }
                        break;

                    case (2):
                        ACLMessage msgSetValues = new ACLMessage(AgentMessages.SET_PROCESS_VALUES);
                        msgSetValues.setContent(msgContent);
                        msgSetValues.addReceiver(new AID(args[0].toString(), AID.ISLOCALNAME));
                        send(msgSetValues);
                        runProcessStep = 3;
                        break;

                    case (3):
                        msgTmp = MessageTemplate.MatchPerformative(AgentMessages.SET_PROCESS_VALUES_ACK);
                        msgReceive = receive(msgTmp);
                        if (msgReceive != null) {
                            runProcessStep = 4;
                        }
                        break;

                    case (4):
                        ACLMessage msgStartProcess = new ACLMessage(AgentMessages.START_PROCESS);
                        msgStartProcess.setContent("");
                        msgStartProcess.addReceiver(new AID(args[0].toString(), AID.ISLOCALNAME));
                        send(msgStartProcess);
                        runProcessStep = 5;
                        break;

                    case (5):
                        msgTmp = MessageTemplate.MatchPerformative(AgentMessages.RECEIVE_RESULT);
                        msgReceive = receive(msgTmp);
                        if (msgReceive != null) {
                            agentResult = msgReceive.getContent();
                            runProcessFinished = true;

                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public boolean done() {
                return runProcessFinished;
            }
        });

        runProcessFinished = false;
        runProcessStep = 0;
        while (agentResult == null) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return agentResult;
    }

    @Override
    public void startTraining() {
        learningFinished = false;
        learningStep = 0;
        addBehaviour(new Behaviour() {
            @Override
            public void action() {

                String msgContent = "Train on new data";
                MessageTemplate msgTmp;
                ACLMessage msgReceive;
                switch (learningStep) {

                    case (0):
                        ACLMessage msgProcessInit = new ACLMessage(AgentMessages.START_LEARNING_AGENT);
                        System.out.println("Started learning agent");
                        msgProcessInit.setContent("");
                        msgProcessInit.addReceiver(new AID(args[0].toString(), AID.ISLOCALNAME));
                        send(msgProcessInit);
                        learningStep = 1;
                        block();

                    case (1):
                        msgTmp = MessageTemplate.MatchPerformative(AgentMessages.START_LEARNING_AGENT_ACK);
                        msgReceive = receive(msgTmp);
                        if (msgReceive != null) {
                            learningStep = 2;
                        }
                        break;

                    case (2):
                        ACLMessage msgSetValues = new ACLMessage(AgentMessages.START_LEARNING);
                        System.out.println("Started learning");
                        msgSetValues.setContent("");
                        msgSetValues.addReceiver(new AID(args[0].toString(), AID.ISLOCALNAME));
                        send(msgSetValues);
                        learningStep = 3;
                        break;

                    case (3):
                        msgTmp = MessageTemplate.MatchPerformative(AgentMessages.START_LEARNING_ACK);
                        msgReceive = receive(msgTmp);
                        if (msgReceive != null) {
                            learningStep = 4;
                            learningFinished = true;
                        }
                        break;

                    default:
                        break;
                }
            }

            @Override
            public boolean done() {
                return learningFinished;
            }
        });

    }

    protected void setup() {
        args = getArguments();

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ArrayList<MessageTemplate> templates = new ArrayList<>();
                templates.add(MessageTemplate.MatchPerformative(AgentMessages.CHECK_AGENT));
                templates.add(MessageTemplate.MatchPerformative(1));
                ACLMessage[] checkMsg = new ACLMessage[templates.size()];

                int counter = 0;
                for (MessageTemplate checkState : templates) {
                    checkMsg[counter++] = receive(checkState);
                }

                for (ACLMessage msg : checkMsg) {
                    if (msg != null) {
                        //confirming that agent is working
                        if (msg.getPerformative() == AgentMessages.CHECK_AGENT) {
                            ACLMessage reply = new ACLMessage(AgentMessages.CHECK_AGENT);
                            reply.setContent("success");
                            reply.addReceiver(new AID(args[0].toString(), AID.ISLOCALNAME));
                            send(reply);
                        } else if (msg.getPerformative() == 1) {
                            System.out.println(msg.getContent());
                        }
                    }

                    block();
                }
            }
        });
    }
}
