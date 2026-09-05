package ru.javarush.iliasov.islandproject.Services;

import ru.javarush.iliasov.islandproject.Cell;
import ru.javarush.iliasov.islandproject.Island;
import ru.javarush.iliasov.islandproject.Organisms.*;

import java.util.*;

public class StatisticsService {
    private final Island island;

    public StatisticsService(Island island) {
        this.island = island;
    }

    public Map<AnimalType, Integer> countAnimalsByType() {
        Map<AnimalType, Integer> animalTypeIntegerMap = new LinkedHashMap<>();
        for (Cell[] cells : island.getCells()) {
            for (Cell cell : cells) {
                for (Animal animal : cell.getAnimalsList()) {
                    animalTypeIntegerMap.merge(animal.getType(), 1, Integer::sum);
                }
            }
        }
        return animalTypeIntegerMap;
    }


    public int getPlantsCount(){
        int plantsCount = 0;
        for (Cell[] cells: island.getCells()){
            for (Cell cell: cells){
                for (Plants plants: cell.getPlantsList()){
                    plantsCount++;
                }
            }
        }
        return plantsCount;
    }

}
