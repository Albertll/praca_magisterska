package agh.generator;

public class MainGen {

    public static void main(String[] args) {
        Generator gen = new Generator();

        //gen.generateRatio(10, Metals.Aluminium, true);
        //gen.generateRatioForAll(10, true);

        int[] metals = {80, 20, 0, 0, 0, 0, 0, 0, 0};
        int[] metals2 = {80, 15, 5, 0, 0, 0, 0, 0, 0};
        int[] metals3 = {80, 12, 2, 6, 0, 0, 0, 0, 0};
        int[] metals4 = {8, 2, 90, 0, 0, 0, 0, 0, 0};
        int[] metals5 = {7, 0, 90, 0, 3, 0, 0, 0, 0};
        int[] metals6 = {0, 0, 0, 98, 0, 2, 0, 0, 0};
        int[] metals7 = {0, 0, 0, 70, 30, 0, 0, 0, 0};
        int[] metals8 = {0, 0, 0, 30, 0, 0, 67, 3, 0};
        int[] metals9 = {0, 0, 0, 2, 0, 3, 0, 0, 95};
        gen.generateQ("AlSi1.arff", metals, 1000, true, true);
        /*gen.generateQ("AlSiMg1.arff", metals2, 10000);
        gen.generateQ("AlSiMgCu1.arff", metals3, 10000);
        gen.generateQ("MgAlSi1.arff", metals4, 10000);
        gen.generateQ("MgAlZn1.arff", metals5, 10000);
        gen.generateQ("CuSn1.arff", metals6, 10000);
        gen.generateQ("CuZn1.arff", metals7, 10000);
        gen.generateQ("NiCuFe1.arff", metals8, 10000);
        gen.generateQ("PbZnCu1.arff", metals9, 10000);*/

    }
}
