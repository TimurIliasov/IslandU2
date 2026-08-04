package ru.javarush.iliasov.cryptoanalyzer.Organisms;

public abstract class Animals {
    private int currentWeight;
    private final AnimalType type;
    private boolean isAlive;
    private AnimalConfig config;


    protected Animals(AnimalType type) {
        this.type = type;

    }
}
