package agh.agents;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;

public class MainContainer {
    public static ContainerController cc = Runtime.instance().createMainContainer(new ProfileImpl(null, 1099, null));
}