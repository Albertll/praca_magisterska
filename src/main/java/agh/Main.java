package agh;

import agh.agents.InterfaceUI;
import agh.classification.ProductionData;
import agh.agents.MainContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static ProductionData productionData = new ProductionData();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
        primaryStage.setTitle("IndustryOptimizer");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) throws IOException, InterruptedException, ControllerException {

        try {
            AgentController prod = MainContainer.cc.createNewAgent("Production-agent",
                    "agh.agents.ProductionAgent", null);
            prod.start();
            AgentController rma = MainContainer.cc.createNewAgent("rma", "jade.tools.rma.rma", null);
            rma.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

        AgentController ac = MainContainer.cc.getAgent("UI-agent");
        InterfaceUI uiObj = ac.getO2AInterface(InterfaceUI.class);
        uiObj.startTraining();

        launch(args);
    }
}
