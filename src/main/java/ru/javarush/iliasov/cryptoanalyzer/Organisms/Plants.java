package ru.javarush.iliasov.cryptoanalyzer.Organisms;

public class Plants {
    private final double weight = 1;
    private static final int MAX_IN_CELL = 200;

    public Plants(){

    }

    public double getWeight() {
        return weight;
    }

    public static int getMaxInCell(){
        return MAX_IN_CELL;
    }

    }


