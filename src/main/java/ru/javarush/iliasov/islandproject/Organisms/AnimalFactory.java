package ru.javarush.iliasov.islandproject.Organisms;

public class AnimalFactory {
    private final ConfigLoader configLoader;

    public AnimalFactory(ConfigLoader configLoader) {
        this.configLoader = configLoader;
    }

    public Animal create(AnimalType type){
        return new Animal(type, configLoader.getConfig(type));
    }
}
