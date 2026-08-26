package ru.javarush.iliasov.cryptoanalyzer.Organisms;

import ru.javarush.iliasov.cryptoanalyzer.Cell;
import ru.javarush.iliasov.cryptoanalyzer.Island;

import java.util.Objects;

public class Animal {
    private double currentWeight;
    private final AnimalType type;
    private boolean isAlive;
    private AnimalConfig config;


    protected Animal(AnimalType type, AnimalConfig config) {
        this.config = config;
        this.currentWeight = config.getStartWeight();
        this.isAlive = true;
        this.type = type;
    }

    public AnimalType getType() {
        return type;
    }

    public AnimalConfig getConfig() {
        return config;
    }

    public double getWeight() {
        return currentWeight;
    }

    public void setWeight(double currentWeight) {
        this.currentWeight = currentWeight;
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public boolean getAlive() {
        return isAlive;
    }

    public void eat(Animal prey) {
        double foodConsumed = prey.getWeight();
        setWeight(getWeight() + foodConsumed);
        prey.setAlive(false);
    }

    public void eatPlant(Plants plants) {
        double foodConsumed = plants.getWeight();
        setWeight(getWeight() + foodConsumed);
    }
}

