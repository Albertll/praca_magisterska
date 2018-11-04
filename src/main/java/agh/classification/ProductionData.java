package agh.classification;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.meta.*;
import weka.classifiers.trees.*;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.*;

public class ProductionData {

    private MultilayerPerceptron mlp;
    private RandomForest forest;
    private M5P m5p;
    private Vote vote;
    private Classifier actualClassifier;

    public ProductionData() {
        mlp = new MultilayerPerceptron();
        forest = new RandomForest();
        m5p = new M5P();
        vote = new Vote();
        vote.setClassifiers(new Classifier[]{new M5P(), new RandomForest(), new MultilayerPerceptron()});
    }

    public MultilayerPerceptron getMlp() {
        return mlp;
    }

    public RandomForest getForest() {
        return forest;
    }

    public M5P getM5p() {
        return m5p;
    }

    public Vote getVote() {
        return vote;
    }

    public void train(String trainFile, Classifier classifier) {
        System.out.println("TrainingStarted");
        try {
            DataSource source1 = new DataSource(trainFile);
            Instances trainData = source1.getDataSet();
            if (trainData.classIndex() == -1)
                trainData.setClassIndex(trainData.numAttributes() - 1);

            classifier.buildClassifier(trainData);

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("TrainingFinished");
    }

    public String test(String trainFile, String testFile, int classifier) {
        try {
            DataSource source1 = new DataSource(trainFile);
            DataSource source2 = new DataSource(testFile);
            Instances trainData = source1.getDataSet();
            Instances testData = source2.getDataSet();
            if (trainData.classIndex() == -1)
                trainData.setClassIndex(trainData.numAttributes() - 1);
            if (testData.classIndex() == -1)
                testData.setClassIndex(testData.numAttributes() - 1);
            Evaluation eval = new Evaluation(trainData);

            setActualClassifier(classifier);

            eval.evaluateModel(actualClassifier, testData);
            return eval.toSummaryString("\nResults: " + actualClassifier.toString() + "\n==================\n", true);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String classify(String unlabeledFile, String labeledFile, int classifier) {
        Instances unlabeled = null;
        try {
            unlabeled = new Instances(new BufferedReader(new FileReader(unlabeledFile)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        unlabeled.setClassIndex(unlabeled.numAttributes() - 1);

        Instances labeled = new Instances(unlabeled);

        setActualClassifier(classifier);

        // label instances
        for (int i = 0; i < unlabeled.numInstances(); i++) {
            double clsLabel = 0;
            try {
                clsLabel = actualClassifier.classifyInstance(unlabeled.instance(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
            labeled.instance(i).setClassValue(clsLabel);
            //System.out.println(clsLabel + " -> " + unlabeled.classAttribute().value((int) clsLabel));
        }
        System.out.println("Unlabeled data :" + unlabeled.toString().split("@data")[1] + "\n");
        System.out.println("Labeled data :" + labeled.toString().split("@data")[1] + "\n");

        String result = labeled.toString().split(",")[16].substring(0, 6);

        // save labeled data
        try {
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(labeledFile));
            writer.write(labeled.toString());
            writer.newLine();
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void setActualClassifier(int classifier) {
        switch (classifier) {
            case 0:
                actualClassifier = mlp;
                break;
            case 1:
                actualClassifier = m5p;
                break;
            case 2:
                actualClassifier = forest;
                break;
            case 3:
                actualClassifier = vote;
                break;
            default:
                actualClassifier = mlp;
                break;

        }
    }
}
