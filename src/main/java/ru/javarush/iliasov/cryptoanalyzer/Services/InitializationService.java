package ru.javarush.iliasov.cryptoanalyzer.Services;

import ru.javarush.iliasov.cryptoanalyzer.Cell;
import ru.javarush.iliasov.cryptoanalyzer.Island;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.*;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.Plants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class InitializationService {
    private final Island island = new Island(new Cell[20][100]);

    public void cellPlant() {
        for (int i = 0; i < island.getCells().length; i++) {
            for (int j = 0; j < island.getCells()[i].length; j++) {
                Cell cell = new Cell(new ArrayList<>(), new ArrayList<>());
                List<Plants> plants = new Plants().plantsInCell();

                for (Plants plant : plants) {
                    cell.addPlant(plant);
                }
                island.getCells()[i][j] = cell;
            }
        }
    }

    public void cellAnimal(ConfigLoader configLoader, AnimalType type) {
        AnimalFactory factory = new AnimalFactory(configLoader);
        AnimalConfig config = configLoader.getConfig(type);
        int max = config.getMaxInCell();
        for (int i = 0; i < island.getCells().length; i++) {
            for (int j = 0; j < island.getCells()[i].length; j++) {
                Cell cell = island.getCells()[i][j];
                int randomCell = ThreadLocalRandom.current().nextInt(0, max + 1);
                for (int k = 0; k < randomCell; k++) {
                    Animal animal = factory.create(type);
                    cell.addAnimal(animal);
                }
            }
        }
    }

    public void startIsland(ConfigLoader configLoader){
        cellPlant();
        for (AnimalType type: AnimalType.values()){
            cellAnimal(configLoader, type);
        }
    }
}

