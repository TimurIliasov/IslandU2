package ru.javarush.iliasov.islandproject.Organisms;

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
        double preyWeight = prey.getWeight();
        setWeight(getWeight() + preyWeight);
        prey.setAlive(false);
    }


    public void eatPlant(Plants plants) {
        double foodConsumed = plants.getWeight();
        setWeight(getWeight() + foodConsumed);
    }
}

