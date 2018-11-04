package agh.controllers;

import agh.agents.InterfaceUI;
import agh.agents.MainContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class PredictController implements Initializable {

    private Controller controller = Controller.getInstance();
    private Map<Integer, int[]> metalsMap = new HashMap<>();
    private TextField[] metalsArray;

    @FXML
    private Pane PredictPane;
    @FXML
    private TextField aluminium;
    @FXML
    private TextField krzem;
    @FXML
    private TextField magnez;
    @FXML
    private TextField miedz;
    @FXML
    private TextField cynk;
    @FXML
    private TextField cyna;
    @FXML
    private TextField nikiel;
    @FXML
    private TextField zelazo;
    @FXML
    private TextField olow;
    @FXML
    private TextField remained;
    @FXML
    private TextField quality;
    @FXML
    private ChoiceBox<String> upgrades;
    @FXML
    private ChoiceBox<String> stops;
    @FXML
    private ChoiceBox<String> classifiers;
    @FXML
    private TextField temp;
    @FXML
    private TextField time;
    @FXML
    private TextField temp1;
    @FXML
    private TextField time1;
    @FXML
    private TextField temp2;
    @FXML
    private TextField time2;
    @FXML
    private TextField mass;
    @FXML
    private TextArea input;
    @FXML
    private TextField cost;

    @FXML
    protected void handleBack(ActionEvent event) {
        controller.handleBack(PredictPane);
    }

    private void checkIfProperData() {
        if (getValue() > 100) {
            new Alert(Alert.AlertType.ERROR, "Niepoprawne wartości metali").showAndWait();
        }
    }

    @FXML
    public void handleProcessing(ActionEvent event) {
        controller.handleProcessing(classifiers.getSelectionModel().getSelectedIndex(), "TrainingData.arff");
    }

    @FXML
    void handlePrediction() throws ControllerException {
        checkIfProperData();
        String[] data = {
                aluminium.getText(),
                krzem.getText(),
                magnez.getText(),
                miedz.getText(),
                cynk.getText(),
                cyna.getText(),
                nikiel.getText(),
                zelazo.getText(),
                olow.getText(),
                temp.getText(),
                time.getText(),
                temp1.getText(),
                time1.getText(),
                temp2.getText(),
                time2.getText(),
                Integer.toString(upgrades.getSelectionModel().getSelectedIndex() + 1),
                mass.getText()
        };

        AgentController ac = MainContainer.cc.getAgent("UI-agent");
        InterfaceUI uiObj = ac.getO2AInterface(InterfaceUI.class);

        String result = uiObj.runProcess(data, classifiers.getSelectionModel().getSelectedIndex());
        String[] results = result.split("-");
        input.setText(results[0]);
        cost.setText(results[1]);
        quality.setText(results[2]);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        initializeAllParameters(); //TEMPORARY FOR TESTING
        initializeTextFields();
        initializeChoiceBoxes();
    }

    private void initMetalsMap() {
        metalsMap.put(0, new int[]{80, 20, 0, 0, 0, 0, 0, 0, 0});
        metalsMap.put(1, new int[]{80, 15, 5, 0, 0, 0, 0, 0, 0});
        metalsMap.put(2, new int[]{80, 12, 2, 6, 0, 0, 0, 0, 0});
        metalsMap.put(3, new int[]{8, 2, 90, 0, 0, 0, 0, 0, 0});
        metalsMap.put(4, new int[]{7, 0, 90, 0, 3, 0, 0, 0, 0});
        metalsMap.put(5, new int[]{0, 0, 0, 98, 0, 2, 0, 0, 0});
        metalsMap.put(6, new int[]{0, 0, 0, 70, 30, 0, 0, 0, 0});
        metalsMap.put(7, new int[]{0, 0, 0, 30, 0, 0, 67, 3, 0});
        metalsMap.put(8, new int[]{0, 0, 0, 2, 0, 3, 0, 0, 95});
    }

    private void initTextFieldsArray() {
        metalsArray = new TextField[]{aluminium, krzem, magnez, miedz, cynk, cyna, nikiel, zelazo, olow};
    }

    private void initializeAllParameters() {
        stops.getSelectionModel().select(2);
        temp.setText("1500");
        time.setText("50");
        temp1.setText("1000");
        time1.setText("15");
        temp2.setText("700");
        time2.setText("25");
        mass.setText("100");
    }

    private void initializeChoiceBoxes() {
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Brak",
                        "Słabe",
                        "Srednie",
                        "Dobre",
                        "Bardzo Dobre"
                );
        upgrades.setItems(options);
        upgrades.getSelectionModel().selectFirst();
        options = FXCollections.observableArrayList(
                "MultilayerPerceptron",
                "M5P",
                "RandomForest",
                "Vote"
        );
        classifiers.setItems(options);
        classifiers.getSelectionModel().selectFirst();
        try {
            initializeChoiceBox();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeChoiceBox() throws IOException {
        ObservableList<String> options = FXCollections.observableArrayList();
        FileReader fileReader;

        try {
            fileReader = new FileReader("Stops.txt");
        } catch (FileNotFoundException e) {
            initDefaultStops();
            return;
        }

        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String[] params;
        int[] percents = new int[9];
        String line = bufferedReader.readLine();
        do {
            params = fromString(line);
            options.add(params[0]);
            for (int i = 1; i < params.length; i++) {
                percents[i - 1] = Integer.parseInt(params[i]);
            }
            line = bufferedReader.readLine();
            addToMetalsMap(percents.clone());
        } while (line != null);

        bufferedReader.close();

        stops.setItems(options);
        stops.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> setStops(newValue.intValue()));

    }

    private void initDefaultStops() throws IOException {
        initMetalsMap();
        ObservableList<String> options = FXCollections.observableArrayList(
                "AlSi",
                "AlSiMg",
                "AlSiCuMg",
                "MgAlSi",
                "MgAlZn",
                "CuSn-brąz",
                "CuSn-mosiądz",
                "NiCuFe",
                "PbSnCu"
        );
        stops.setItems(options);
        stops.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> setStops(newValue.intValue()));
        FileWriter writer = new FileWriter("Stops.txt");
        for (int i = 0; i < options.size() - 1; i++)
            writer.write(options.get(i) + ", " + Arrays.toString(metalsMap.get(i)) + "\n");
        writer.write(options.get(options.size() - 1) + ", " + Arrays.toString(metalsMap.get(options.size() - 1)));
        writer.close();
    }

    private String[] fromString(String string) {
        String[] strings = string.replace("[", "").replace("]", "").split(", ");
        String result[] = new String[strings.length];
        System.arraycopy(strings, 0, result, 0, result.length);
        return result;
    }

    private void initializeTextFields() {
        initTextFieldsArray();
        for (TextField metal : metalsArray) {
            metal.textProperty().addListener((observable, oldValue, newValue) -> remained.setText(String.valueOf(100 - getValue())));
        }
    }

    private double getValue() {
        double sum = 0;
        for (TextField aMetalsArray : metalsArray)
            if (aMetalsArray.getText() != null && !aMetalsArray.getText().trim().isEmpty())
                sum += Double.parseDouble(aMetalsArray.getText());
        return sum;
    }

    public void setScene(Stage stage, Parent root) {
        stage.setTitle("Predict");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void setStops(int i) {
        setTextFields(metalsMap.get(i));
    }

    private void setTextFields(int[] metals) {
        for (int i = 0; i < metalsArray.length; i++)
            metalsArray[i].setText(Integer.toString(metals[i]));
    }

    private void addToMetalsMap(int[] array) {
        metalsMap.put(metalsMap.size(), array);
    }


    static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

}
