package agh.calculation;

import agh.generator.Metals;

import java.util.Locale;

public class Calculator {
    private double cost;
    private double actualMass;

    public Calculator() {
    }

    public String calculateInput(String[] data) {
        StringBuilder result = new StringBuilder();
        double mass = Double.parseDouble(data[data.length - 1]);
        double temp = Double.parseDouble(data[Metals.values().length]);
        cost = 0;
        actualMass = 0;
        for (int i = 0; i < Metals.values().length; i++) {

            double tmp = (mass * (Double.parseDouble(data[i]) / 100.0) * ((100.0 + Metals.getRatio(Metals.values()[i]) + (temp / 2000.0)) / 100.0));
            cost += tmp * Metals.getPrice(Metals.values()[i]);
            actualMass += tmp;
            result.append(Metals.values()[i].toString()).append(" : ").append(String.format(Locale.US, "%.2f", tmp)).append("\n");
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    public String calculateCost(String[] data) {
        double temp = Double.parseDouble(data[Metals.values().length]);
        double time = Double.parseDouble(data[Metals.values().length + 1]);
        double upgrade = Double.parseDouble(data[data.length - 2]);

        cost += time * temp / 1000 * 10 + upgrade * actualMass;

        return String.format(Locale.US, "%.2f", cost);
    }
}
