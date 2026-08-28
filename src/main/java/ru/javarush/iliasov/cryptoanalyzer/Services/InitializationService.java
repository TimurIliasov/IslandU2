package ru.javarush.iliasov.cryptoanalyzer.Services;

import ru.javarush.iliasov.cryptoanalyzer.Cell;
import ru.javarush.iliasov.cryptoanalyzer.Island;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.*;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.Plants;

import java.util.ArrayList;

public class InitializationService {
    private final RandomService randomService;
    private final ConfigLoader configLoader;
    private final AnimalFactory factory;

    public InitializationService(RandomService randomService, ConfigLoader configLoader, AnimalFactory factory) {
        this.randomService = randomService;
        this.configLoader = configLoader;
        this.factory = factory;
    }

    public void cellPlant(Island island) {
        for (int i = 0; i < island.getCells().length; i++) {
            for (int j = 0; j < island.getCells()[i].length; j++) {
                Cell cell = new Cell(new ArrayList<>(), new ArrayList<>());
                int random = randomService.getRandomNumber(Plants.getMaxInCell());
                for (int k = 0; k < random; k++) {
                    Plants plants = new Plants();
                    cell.addPlant(plants);
                }

                island.getCells()[i][j] = cell;
            }
        }
    }

    public void cellAnimal(AnimalType type, AnimalConfig config, Island island) {
        for (int i = 0; i < island.getCells().length; i++) {
            for (int j = 0; j < island.getCells()[i].length; j++) {
                Cell cell = island.getCells()[i][j];
                int randomAnimalCount = randomService.getRandomNumber(config.getMaxInCell());
                for (int k = 0; k < randomAnimalCount; k++) {
                    Animal animal = factory.create(type);
                    cell.addAnimal(animal);
                }
            }
        }
    }

    public void startIsland(Island island){
        cellPlant(island);
        for (AnimalType type: AnimalType.values()){
            cellAnimal(type, configLoader.getConfig(type), island);
        }
    }
}

