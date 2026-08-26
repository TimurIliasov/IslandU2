package ru.javarush.iliasov.cryptoanalyzer.Organisms;

public class AnimalFactory {
    private final ConfigLoader configLoader;

    public AnimalFactory(ConfigLoader configLoader) {
        this.configLoader = configLoader;
    }

    public Animal create(AnimalType type){
        Animal animal = new Animal(type, configLoader.getConfig(type));
        animal.setAlive(true);
        return animal;
    }
}
