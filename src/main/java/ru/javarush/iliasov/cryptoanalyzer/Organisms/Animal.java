package ru.javarush.iliasov.cryptoanalyzer.Organisms;

public class Animal {
    private int currentWeight;
    private final AnimalType type;
    private boolean isAlive;
    private AnimalConfig config;


    protected Animal(AnimalType type, AnimalConfig config) {
        this.config = config;
        this.currentWeight = currentWeight;
        this.isAlive = isAlive;
        this.type = type;
    }

    public AnimalType getType(){
        return type;
    }

    public AnimalConfig getConfig(){
        return config;
    }
}
