package agh.agents;

import agh.calculation.Calculator;
import agh.classification.WekaManager;

public class ProductionProcess {

    private String[] data;
    private int classifier;
    private Calculator calculator;


    public ProductionProcess(String[] data, int classifier) {
        this.data = data;
        this.classifier = classifier;
        calculator = new Calculator();
    }

    private String firstStep() {    //wyliczenie rzeczywistego składu
        return calculator.calculateInput(data);
    }

    private String secondStep() {
        return calculator.calculateCost(data);
    }

    private String thirdStep() {     //wyznaczenie jakości
        return WekaManager.makePrediction(data, classifier);
    }

    public String[] runProcess() {
        String[] result = new String[3];

        result[0] = firstStep();
        result[1] = secondStep();
        result[2] = thirdStep();

        return result;
    }
}
