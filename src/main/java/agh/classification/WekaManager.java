package agh.classification;

import agh.Main;
import agh.generator.Generator;

public class WekaManager {

    public static String makePrediction(String[] data, int classifier) {

        StringBuilder line = new StringBuilder();
        for (int i = 0; i < data.length - 1; i++)
            line.append(data[i]).append(",");
        line.append("?");

        Generator.printLine("Prediction.arff", line.toString());
        String quality = Main.productionData.classify("Prediction.arff", "Predicted.arff", classifier);

        return quality;
    }

    public static void makeClassification(String fileToClassify, String classfiedFile, int classifier){
        Main.productionData.classify(fileToClassify, classfiedFile, classifier);
    }

    public static String makeTest(String testFile, int classifier) {
        String result = Main.productionData.test("TrainingData.arff", testFile, classifier);
        return result;
    }
}
