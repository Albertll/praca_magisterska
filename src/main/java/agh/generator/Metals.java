package agh.generator;

public enum Metals {
    Aluminium(1),
    Krzem(2),
    Magnez(3),
    Miedz(4),
    Cynk(5),
    Cyna(6),
    Nikiel(7),
    Zelazo(8),
    Olow(9);

    private final int value;

    Metals(int i) {
        value = i;
    }

    public int getValue() {
        return value;
    }

    public static double getRatio(Metals metal) {
        switch (metal) {
            case Aluminium:
                return 2;
            case Miedz:
                return 1.5;
            case Zelazo:
                return 1;
            case Cynk:
                return 2.3;
            case Olow:
                return 2.5;
            case Cyna:
                return 2.8;
            case Magnez:
                return 2;
            case Krzem:
                return 1;
            case Nikiel:
                return 1;
            default:
                return -1;

        }
    }

    public static int getMinTemperature(Metals metal) {
        switch (metal) {
            case Aluminium:
                return 660;
            case Miedz:
                return 1084;
            case Nikiel:
                return 1453;
            case Cynk:
                return 420;
            case Olow:
                return 327;
            case Cyna:
                return 232;
            case Magnez:
                return 650;
            case Zelazo:
                return 1500;
            case Krzem:
                return 1410;
            default:
                return -1;
        }

    }

    public static double getPrice(Metals metal) {
        switch (metal) {
            case Aluminium:
                return 7.5;
            case Miedz:
                return 22;
            case Nikiel:
                return 48;
            case Cynk:
                return 9.5;
            case Olow:
                return 8;
            case Cyna:
                return 73;
            case Magnez:
                return 32;
            case Zelazo:
                return 0.5;
            case Krzem:
                return 40;
            default:
                return -1;
        }

    }
}
