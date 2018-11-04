package agh.agents;

import agh.classification.ProductionData;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import weka.classifiers.Classifier;

import java.util.ArrayList;

import static agh.Main.productionData;

public class LearningAgent extends Agent {

    private Classifier[] classififiers = new Classifier[]{productionData.getMlp(), productionData.getForest(), productionData.getM5p(), productionData.getVote()};

    protected void setup() {
        Object[] args = getArguments();

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage reply;

                ArrayList<MessageTemplate> templates = new ArrayList<>();
                templates.add(MessageTemplate.MatchPerformative(AgentMessages.CHECK_AGENT));
                templates.add(MessageTemplate.MatchPerformative(AgentMessages.START_LEARNING_MLP));
                templates.add(MessageTemplate.MatchPerformative(AgentMessages.START_LEARNING_M5P));
                templates.add(MessageTemplate.MatchPerformative(AgentMessages.START_LEARNING_FOREST));
                templates.add(MessageTemplate.MatchPerformative(AgentMessages.START_LEARNING_VOTE));

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
                            case (AgentMessages.START_LEARNING_MLP):
                                System.out.println("Training mlp in agent");
                                productionData.train("TrainingData.arff", classififiers[0]);
                                reply = new ACLMessage(AgentMessages.START_LEARNING_MLP_ACK);
                                System.out.println("Training mlp done");
                                reply.setContent("success ");
                                reply.addReceiver(new AID(args[0].toString(), AID.ISLOCALNAME));
                                send(reply);
                                break;
                            case (AgentMessages.START_LEARNING_M5P):
                                System.out.println("Training m5p in agent");
                                productionData.train("TrainingData.arff", classififiers[2]);
                                reply = new ACLMessage(AgentMessages.START_LEARNING_M5P_ACK);
                                System.out.println("Training m5p done");
                                reply.setContent("success ");
                                reply.addReceiver(new AID(args[0].toString(), AID.ISLOCALNAME));
                                send(reply);
                                break;
                            case (AgentMessages.START_LEARNING_FOREST):
                                System.out.println("Training forest in agent");
                                productionData.train("TrainingData.arff", classififiers[1]);
                                reply = new ACLMessage(AgentMessages.START_LEARNING_FOREST_ACK);
                                System.out.println("Training forest done");
                                reply.setContent("success ");
                                reply.addReceiver(new AID(args[0].toString(), AID.ISLOCALNAME));
                                send(reply);
                                break;
                            case (AgentMessages.START_LEARNING_VOTE):
                                System.out.println("Training vote in agent");
                                productionData.train("TrainingData.arff", classififiers[3]);
                                reply = new ACLMessage(AgentMessages.START_LEARNING_VOTE_ACK);
                                System.out.println("Training vote done");
                                reply.setContent("success ");
                                reply.addReceiver(new AID(args[0].toString(), AID.ISLOCALNAME));
                                send(reply);
                                break;
                        }
                    }
                }

                block();
            }
        });
    }
}
