package agh.generator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Generator {
    private static PrintWriter writer;

    public static void generateRatioForAll(int quantity, boolean labeled) {
        for (Metals metal : Metals.values()) {
            generateRatio(quantity, metal, labeled);
        }
    }

    public static void generateRatio(int quantity, Metals metal, boolean labeled) {
        String filename = "w" + metal.toString() + ".arff";
        try {
            writer = new PrintWriter(filename, "UTF-8");
            writer.print("@RELATION " + filename + "\n\n");
            writer.print("@ATTRIBUTE temperatura numeric\n");
            writer.print("@ATTRIBUTE Aluminium numeric\n");
            writer.print("@ATTRIBUTE Miedz numeric\n");
            writer.print("@ATTRIBUTE Nikiel numeric\n");
            writer.print("@ATTRIBUTE Cynk numeric\n");
            writer.print("@ATTRIBUTE Olow numeric\n");
            writer.print("@ATTRIBUTE Cyna numeric\n");
            writer.print("@ATTRIBUTE Magnez numeric\n");
            writer.print("@ATTRIBUTE Zelazo numeric\n");
            writer.print("@ATTRIBUTE Krzem numeric\n");
            writer.print("@ATTRIBUTE w" + metal.toString() + " numeric\n");

            writer.print("\n@DATA");

            System.out.println("generation started");
            Random r = new Random();
            int temperature;
            double ratio;
            for (int j = 0; j < quantity; j++) {
                ArrayList load = n_random(9);
                writer.print("\n");
                temperature = r.nextInt(1500) + 500;
                writer.print(temperature);
                writer.print(",");
                for (Object aLoad : load) {
                    writer.print(aLoad);
                    writer.print(",");
                }
                if (temperature < Metals.getMinTemperature(metal)) {
                    ratio = 0;
                } else if (temperature < 1000) {
                    ratio = Metals.getRatio(metal);
                } else {
                    ratio = Metals.getRatio(metal) + (temperature / 2000.0);
                }
                if (labeled) writer.print(String.format(Locale.US, "%.2f", ratio));
                else writer.print("?");

            }
            writer.close();
        } catch (IOException e) {
            writer.close();
        }
        System.out.println("generation ended");
    }

    public static void generateQualityForAllStops(int quantity, boolean labeled, boolean numeric) {
        int[] alsi = {80, 20, 0, 0, 0, 0, 0, 0, 0};
        int[] alsimg = {80, 15, 5, 0, 0, 0, 0, 0, 0};
        int[] alsimgcu = {80, 12, 2, 6, 0, 0, 0, 0, 0};
        int[] mgalsi = {8, 2, 90, 0, 0, 0, 0, 0, 0};
        int[] mgalzn = {7, 0, 90, 0, 3, 0, 0, 0, 0};
        int[] cusn = {0, 0, 0, 98, 0, 2, 0, 0, 0};
        int[] cuzn = {0, 0, 0, 70, 30, 0, 0, 0, 0};
        int[] nicufe = {0, 0, 0, 30, 0, 0, 67, 3, 0};
        int[] pbzncu = {0, 0, 0, 2, 0, 3, 0, 0, 95};
        generateQ("AlSi.arff", alsi, quantity, labeled, numeric);
        generateQ("AlSiMg.arff", alsimg, quantity, labeled, numeric);
        generateQ("AlSiMgCu.arff", alsimgcu, quantity, labeled, numeric);
        generateQ("MgAlSi.arff", mgalsi, quantity, labeled, numeric);
        generateQ("MgAlZn.arff", mgalzn, quantity, labeled, numeric);
        generateQ("CuSn.arff", cusn, quantity, labeled, numeric);
        generateQ("CuZn.arff", cuzn, quantity, labeled, numeric);
        generateQ("NiCuFe.arff", nicufe, quantity, labeled, numeric);
        generateQ("PbZnCu.arff", pbzncu, quantity, labeled, numeric);
    }

    private static ArrayList n_random(int n) {
        Random r = new Random();
        ArrayList<Integer> load = new ArrayList<>();
        int temp;
        int sum = 0;
        for (int i = 1; i <= n; i++) {
            if (!(i == n)) {
                temp = r.nextInt((100 - sum) / (n - i)) + 1;
                load.add(temp);
                sum += temp;

            } else {
                int last = (100 - sum);
                load.add(last);
                sum += last;
            }
        }
        return load;
    }

    private static int getHighestTemperatureArray(int[] metalList) {
        int temperature = 0;
        for (Metals metal : Metals.values()) {
            if (metalList[metal.getValue() - 1] != 0) {
                if (Metals.getMinTemperature(metal) > temperature)
                    temperature = Metals.getMinTemperature(metal);
            }
        }
        return temperature;
    }

    private static void printHeader(String filename, boolean numeric) {
        try {
            writer = new PrintWriter(filename, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            writer.close();
        }
        writer.print("@RELATION " + filename + "\n\n");
        writer.print("@ATTRIBUTE Aluminium numeric\n");
        writer.print("@ATTRIBUTE Krzem numeric\n");
        writer.print("@ATTRIBUTE Magnez numeric\n");
        writer.print("@ATTRIBUTE Miedz numeric\n");
        writer.print("@ATTRIBUTE Cynk numeric\n");
        writer.print("@ATTRIBUTE Cyna numeric\n");
        writer.print("@ATTRIBUTE Nikiel numeric\n");
        writer.print("@ATTRIBUTE Zelazo numeric\n");
        writer.print("@ATTRIBUTE Olow numeric\n");
        writer.print("@ATTRIBUTE TemperaturaWytapiania numeric\n");
        writer.print("@ATTRIBUTE CzasWytapiania numeric\n");
        writer.print("@ATTRIBUTE TemperaturaStudzenia numeric\n");
        writer.print("@ATTRIBUTE CzasPodgrzewania numeric\n");
        writer.print("@ATTRIBUTE TemperaturaStudzenia2 numeric\n");
        writer.print("@ATTRIBUTE CzasPodgrzewania2 numeric\n");
        writer.print("@ATTRIBUTE StopieńUszlachetniania numeric\n");
        writer.print("@ATTRIBUTE Jakość ");
        printType(numeric);
        writer.print("\n\n@DATA");
    }

    private static void printType(boolean numeric) {
        if (numeric)
            writer.write("numeric");
        else {
            writer.print("{" + 0);
            for (int i = 1; i < 101; i++)
                writer.print("," + i);
            writer.print("}\n");
        }

    }

    public static void printLine(String filename, String line) {
        printHeader(filename, true);
        writer.print("\n" + line);
        writer.close();
    }

    private static void writeToFile(int[] metals, int temperature, int time, int coolingTemperature, int heatingTime, int coolingTemperature2, int heatingTime2, int level, int quality, boolean labeled) {
        writer.print("\n");

        for (int metal : metals) {
            writer.print(metal);
            writer.print(",");
        }

        writer.print(temperature);
        writer.print(",");

        writer.print(time);
        writer.print(",");

        writer.print(coolingTemperature);
        writer.print(",");

        writer.print(heatingTime);
        writer.print(",");

        writer.print(coolingTemperature2);
        writer.print(",");

        writer.print(heatingTime2);
        writer.print(",");

        writer.print(level);
        writer.print(",");

        if (labeled) writer.print(quality);
        else writer.print("?");
    }

    private static Integer[] gen(Random r, int temperatureLevel) {
        int quality = 90;
        int temperatureBound = (int) (0.7 * temperatureLevel);
        int minTemperature = (int) (0.5 * temperatureLevel);
        int temperature = (r.nextInt(temperatureBound) + minTemperature);

        if (temperature >= temperatureLevel) {
            quality += (temperature - temperatureLevel) / ((minTemperature + temperatureBound - temperatureLevel) / 10);
        } else {
            quality -= (temperatureLevel - temperature) / (temperatureLevel * 0.5 / 90);
        }

        int time = (r.nextInt(40) + 30);

        if (quality != 0)
            quality -= Math.abs(time - 50) / (20.0 / (0.6 * quality));

        int optimalCoolingTemperature = (int) (0.6666 * temperature);

        int coolingTemperature = (r.nextInt((int) (0.25 * temperature)) + optimalCoolingTemperature - (int) (0.25 * temperature) / 2);

        if (quality != 0)
            quality -= Math.abs(coolingTemperature - optimalCoolingTemperature) / (((0.25 * temperature) / 2) / (0.3 * quality));

        int heatingTime = (r.nextInt(20) + 5);

        if (quality != 0)
            quality -= Math.abs(heatingTime - 15) / (10.0 / (0.1 * quality));

        int optimalCoolingTemperature2 = (int) (0.5 * optimalCoolingTemperature);

        int coolingTemperature2 = (r.nextInt((int) (0.25 * coolingTemperature)) + optimalCoolingTemperature2 - (int) (0.25 * optimalCoolingTemperature) / 2);

        if (quality != 0)
            quality -= Math.abs(coolingTemperature2 - optimalCoolingTemperature2) / (((0.25 * optimalCoolingTemperature) / 2) / (0.1 * quality));

        int heatingTime2 = (r.nextInt(30) + 10);

        if (quality != 0)
            quality -= Math.abs(heatingTime2 - 25) / (15.0 / (0.05 * quality));

        int level = (r.nextInt(5) + 1);

        switch (level) {
            case 1:
                quality += 0;
                break;
            case 2:
                quality += 2;
                break;
            case 3:
                quality += 4;
                break;
            case 4:
                quality += 6;
                break;
            case 5:
                quality += 8;
        }
        Integer[] result = new Integer[]{temperature, time, coolingTemperature, heatingTime, coolingTemperature2, heatingTime2, level, quality};
        return result;
    }

    public static void generateQ(String filename, int[] metals, int quantity, boolean labeled, boolean numeric) {
        printHeader(filename, numeric);
        System.out.println("start");
        Random r = new Random();
        int temperatureLevel = getHighestTemperatureArray(metals);

        for (int k = 0; k < 100; k += 10) {
            int j = 0;
            while (j != quantity / 10) {

                Integer[] res = gen(r, temperatureLevel);
                int quality = res[7];

                if (quality >= k && quality <= k + 10) {
                    j++;
                    writeToFile(metals, res[0], res[1], res[2], res[3], res[4], res[5], res[6], quality, labeled);
                }
            }
        }

        int l = quantity % 10;

        for (int i = 0; i < l; i++) {
            Integer[] res = gen(r, temperatureLevel);
            int quality = res[7];

            writeToFile(metals, res[0], res[1], res[2], res[3], res[4], res[5], res[6], quality, labeled);
        }

        writer.close();
        System.out.println("koniec");
    }
}


