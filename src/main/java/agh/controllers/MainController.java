package agh.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    private Pane mainPane;

    @FXML
    protected void handleTesting(ActionEvent event) {

        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/fxml/testing.fxml"));
        Parent root;
        try {
            root = (Parent) loader.load();
            TestingController controller = (TestingController) loader.getController();
            controller.setScene((Stage) mainPane.getScene().getWindow(), root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handlePrediction(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/fxml/predict.fxml"));
        Parent root;
        try {
            root = (Parent) loader.load();
            PredictController controller = (PredictController) loader.getController();
            controller.setScene((Stage) mainPane.getScene().getWindow(), root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleGenerateData(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/fxml/generator.fxml"));
        Parent root;
        try {
            root = (Parent) loader.load();
            GeneratorController controller = (GeneratorController) loader.getController();
            controller.setScene((Stage) mainPane.getScene().getWindow(), root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setScene(Stage stage, Parent root) {
        stage.setTitle("IndustryOptimizer");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
