package agh.agents;

import agh.CPUUtils;
import agh.classification.ProductionData;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.lazy.KStar;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.Vote;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.rules.M5Rules;
import weka.classifiers.trees.M5P;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.util.*;
import java.util.stream.Collectors;

public class LearningAgent extends Agent {

    public static int Iteration;

    protected void setup() {

        List<Classifier> classifiers = new ArrayList<Classifier>();
        List<ProductionData> productionDatas = new ArrayList<ProductionData>();


        classifiers.add(new MultilayerPerceptron());
        classifiers.add(new M5P());
        classifiers.add(new RandomForest());
        Vote vote = new Vote();
        vote.setClassifiers(new Classifier[]{new M5P(), new RandomForest(), new MultilayerPerceptron()});
        classifiers.add(vote);
        classifiers.add(new Bagging());
        classifiers.add(new REPTree());
        classifiers.add(new DecisionTable());
        classifiers.add(new KStar());
        classifiers.add(new M5Rules());
        RandomTree r = new RandomTree();
        classifiers.add(r);

        for (Classifier classifier : classifiers) {
            ProductionData productionData = new ProductionData();
            productionData.train(DataSetManager.dataSource, classifier);
            productionDatas.add(productionData);
        }


        ConverterUtils.DataSource source1 = null;
        Instances trainData = null;
        try {
            source1 = new ConverterUtils.DataSource(DataSetManager.dataSource);
            trainData = source1.getDataSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (trainData.classIndex() == -1)
            trainData.setClassIndex(trainData.numAttributes() - 1);


        Map<String, List<String>> dataSourceParameters = DataSetManager.getParameters(DataSetManager.dataSource);
        List<Map<String, String>> mixedValues = DataSetManager.getMixedValues(DataSetManager.dataSource);


        Random rand = new Random();


        //int minQuality = 10;
        for (double minQuality = DataSetManager.minQuality; minQuality <= DataSetManager.maxQuality; minQuality++) {
            int statesSize = mixedValues.size();
            int actions = 50;
             double discountFactor = 0.95;
             double learningRate = 0.25;


            double[][] qMatrix = new double[statesSize][];
            for (int i = 0; i < statesSize; i++) {
                qMatrix[i] = new double[actions];
            }


            for (int iterN = 0; iterN < DataSetManager.learningSubsystemIterations; iterN++) {

                int i = rand.nextInt(mixedValues.size());
                Map<String, String> mixedValue = mixedValues.get(i);

                double quality = DataSetManager.getQuality(DataSetManager.dataSource, mixedValue);
                qMatrix[i][0] = quality;
                double dQ = quality - minQuality;
                qMatrix[i][2] = dQ;

                double cost = DataSetManager.getCost(mixedValue);
                qMatrix[i][1] = cost;

                double baseCost = DataSetManager.baseCost;

                // qMatrix[i][3] = cost + (dQ < 0 ? 300 : 0);
                double deltaCost = cost * (quality < minQuality ? 1.5 : 1) - baseCost;
                qMatrix[i][3] = deltaCost;
                deltaCost = 2 * baseCost - cost * (quality < minQuality ? 1.5 : 1);

                double qualityWage = DataSetManager.learningSubsystemQualityWage;


                Instance instance = trainData.get(i);
                try {
                    int ci = rand.nextInt(classifiers.size());

                    double classifiedQuality = classifiers.get(ci).classifyInstance(instance);

                    double deltaQuality = -(Math.abs(1 - quality / classifiedQuality)) * 100;


                    deltaCost = 2 * baseCost - cost * (classifiedQuality < minQuality ? 1.5 : 1);

                    qMatrix[i][4 + ci] = classifiedQuality;
                    qMatrix[i][15 + ci] = deltaQuality;
                    qMatrix[i][15 + ci] = deltaQuality * qualityWage + deltaCost * (1 - qualityWage);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            double[] max = new double[16];
            double maxx = -1;
            int maxi = 999999;
            double maxiq = -1;
            for (double[] qvalue : qMatrix) {
                for (int i = 0; i < classifiers.size(); i++)
                    if (qvalue[15 + i] > maxx) {
                        max = qvalue;
                        maxx = qvalue[15 + i];
                        maxi = i;
                        maxiq = qvalue[4 + i];
                    }
            }

            System.out.println(minQuality +
                    "\t" + max[0] +
                    "\t" + maxiq +
                    "\t" + max[1] +
                    "\t" + maxx +
                    ", " + classifiers.get(maxi).getClass().getSimpleName());
        }

        System.out.println("end");


    }
}

