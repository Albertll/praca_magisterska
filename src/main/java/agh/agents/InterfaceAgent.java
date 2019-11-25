package agh.agents;

import agh.CPUUtils;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class InterfaceAgent extends Agent implements InterfaceUI {

    private Object[] args;

    public InterfaceAgent() {
        registerO2AInterface(InterfaceUI.class, this);
    }

    @Override
    public void startTraining() {

        //CPUUtils.sleep(1000);

        addBehaviour(new Behaviour() {
            @Override
            public void action() {

                System.out.println("UI: Starting test");

                ACLMessage msgProcessInit = new ACLMessage(ACLMessage.PROPOSE);
                msgProcessInit.setContent("");
                msgProcessInit.addReceiver(new AID(args[0].toString(), AID.ISLOCALNAME));
                send(msgProcessInit);
                block();
            }

            @Override
            public boolean done() {
                return true;
            }
        });
    }

    protected void setup() {
        args = getArguments();
    }
}
