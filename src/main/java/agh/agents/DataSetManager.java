package agh.agents;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

public class DataSetManager {

    public static String dataSource;
    public static int maxCost = -1;
    public static double minQuality = 15;
    public static double maxQuality = 15;
    public static int baseCost = -1;
    public static int subsystemTypeId = 1;
    public static double learningSubsystemQualityWage = 0.5;
    public static double learningSubsystemIterations = 1000;
    private static Properties appProps;

    public static void Init() {
        appProps = getPropertyResource();

        dataSource = appProps.getProperty("dataFile");
        learningSubsystemQualityWage = Double.parseDouble(appProps.getProperty("learningSubsystemQualityWage"));
        subsystemTypeId = Integer.parseInt(appProps.getProperty("subsystemTypeId"));

        minQuality = maxQuality = Integer.parseInt(appProps.getProperty("minQuality"));
        if (appProps.getProperty("maxQuality") != null)
            maxQuality = Integer.parseInt(appProps.getProperty("maxQuality"));
    }

    public static int getCost(Map<String,String> agentView) {

        int totalCost = baseCost = Integer.parseInt(appProps.getProperty("baseCost"));

        for (Map.Entry<String, String> stringStringEntry : agentView.entrySet()) {
            String property = appProps.getProperty(stringStringEntry.getKey() + "." + stringStringEntry.getValue());
            if (property != null)
                totalCost += Integer.parseInt(property);
        }

        return totalCost;
    }

    private static Properties getPropertyResource() {
        if (appProps != null)
            return appProps;

        InputStream resourceAsStream = DataSetManager.class.getResourceAsStream("/parameters.config");

        appProps = new Properties();
        try {
            appProps.load(new InputStreamReader(resourceAsStream, Charset.forName("UTF-8")));
        } catch (IOException e) { }

        //

        return appProps;
    }


    public static Map<String, List<String>> getParameters(String dataSourcePath) {

        Map<String, List<String>> parametersMap = new HashMap<>();


        try {
            ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource(dataSourcePath);

            Instances dataSet = dataSource.getDataSet();

            Enumeration<Attribute> en = dataSet.enumerateAttributes();

            while (en.hasMoreElements()) {

                Attribute attr = en.nextElement();

                List<String> values = new ArrayList<>();

                Enumeration<Object> en2 = attr.enumerateValues();
                if (en2 == null)
                    continue;

                while (en2.hasMoreElements()) {
                    String param = en2.nextElement().toString();
                    values.add(param);
                }

                parametersMap.put(attr.name(), values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parametersMap;
    }

    public static List<Map<String, String>> getMixedValues (String dataSourcePath) {

        List<Map<String, String>> result = new ArrayList<>();

        try {
            ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource(dataSourcePath);

            Instances dataSet = dataSource.getDataSet();


            for (Instance data : dataSet) {

                Map<String, String> r = new HashMap<>();

                Enumeration<Attribute> attr = dataSet.enumerateAttributes();
                while (attr.hasMoreElements()){
                    Attribute atr = attr.nextElement();
                    String atrV = atr.value((int)data.value(atr));
                    r.put(atr.name(), atrV);
                }

                result.add(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
    public static List<Map<String, String>> getMixedValues9 (Map<String, List<String>> values) {

        List<Map<String, String>> result = new ArrayList<>();

        getMixedValuesCore9(values, result, new HashMap<>(),0);

        return result;
    }

    private static void getMixedValuesCore9(Map<String, List<String>> values, List<Map<String, String>> result, Map<String, String> current, int i) {

        if (values.size() == i){
            result.add(new HashMap<>(current));
            return;
        }

        String x = (String)values.keySet().toArray()[i];

        for (String s : values.get(x)) {
            current.put(x, s);

            getMixedValuesCore9(values, result, current, i + 1);

            current.remove(x);
        }
    }


    public static List<Map<String, String>> getConstraints(String dataSourcePath) {

        Map<String, List<String>> parametersMap = getParameters(dataSourcePath);
        List<Map<String, String>> constraints = new ArrayList<>();

        try {
            ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource(dataSourcePath);

            Instances dataSet = dataSource.getDataSet();


            for (Instance data : dataSet) {

                if (data.value(dataSet.attribute("Jakość")) > minQuality)
                    continue;

                Map<String, String> m = new HashMap<>();

                for (String paramName : parametersMap.keySet()) {

                    Attribute attr = dataSet.attribute(paramName);


                    m.put(paramName, attr.value((int)data.value(attr)));
                }

                constraints.add(m);
            }

            for (String parameterName : parametersMap.keySet()) {
                Map<Integer, Integer> hashes = new HashMap<>();

                for (Map<String, String> stringStringMap : new ArrayList<>(constraints)) {
                    int hash = getHash(parametersMap, parameterName, stringStringMap);

                    if (!hashes.containsKey(hash))
                        hashes.put(hash, 0);

                    int count = hashes.get(hash) + 1;

                    hashes.put(hash, count);

                    if (count == parametersMap.get(parameterName).size() && count != constraints.size()) {
                        optimizeConstraint(parametersMap, hash, constraints, parameterName);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return constraints;
    }

    private static void optimizeConstraint(Map<String, List<String>> parametersMap, int hash0, List<Map<String, String>> constraints, String parameterName) {

        boolean addedReplacement = false;

        for (Map<String, String> stringStringMap : new ArrayList<>(constraints)) {
            int hash = getHash(parametersMap, parameterName, stringStringMap);

            if (hash == hash0) {

                if (addedReplacement)
                    constraints.remove(stringStringMap);
                else {
                    stringStringMap.remove(parameterName);
                    addedReplacement = true;
                }
            }
        }
    }

    private static int getHash(Map<String, List<String>> parametersMap, String parameterName, Map<String, String> stringStringMap) {
        int hash = 0;
        int power = 1;

        for (Map.Entry<String, List<String>> stringListEntry : parametersMap.entrySet()) {
            if (parameterName == stringListEntry.getKey())
                continue;

            hash += power * stringListEntry.getValue().indexOf(stringStringMap.get(stringListEntry.getKey()));
            power *= 10;
        }

        return hash;
    }




    public static double getQuality(String dataSourcePath, Map<String, String> parameters) {

        try {
            ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource(dataSourcePath);

            Instances dataSet = dataSource.getDataSet();

            for (Instance data : dataSet) {

                boolean good = true;

                for (Map.Entry<String, String> paramPair : parameters.entrySet()) {

                    Attribute attr = dataSet.attribute(paramPair.getKey());

                    if (!paramPair.getValue().equals(attr.value((int)data.value(attr))))
                        good = false;
                }

                if (!good)
                    continue;

                return data.value(dataSet.attribute("Jakość"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}
