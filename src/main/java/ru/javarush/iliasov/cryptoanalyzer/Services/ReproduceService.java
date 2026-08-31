package ru.javarush.iliasov.cryptoanalyzer.Services;

import ru.javarush.iliasov.cryptoanalyzer.Cell;
import ru.javarush.iliasov.cryptoanalyzer.Island;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.Animal;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.AnimalFactory;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ReproduceService {
    private final AnimalFactory factory;
    private final Island island;

    public ReproduceService(AnimalFactory factory, Island island) {
        this.factory = factory;
        this.island = island;
    }

    public boolean hasSpaceInCell(Animal animal, Cell cell){
        long currentNumberOfSpecies = cell.getCurrentAnimalCount(animal.getType());
        return animal.getConfig().getMaxInCell() > currentNumberOfSpecies;
    }

    public boolean hasAtLeastTwoAnimals(Animal animal, Cell cell){
        long currentNumberOfSpecies = cell.getCurrentAnimalCount(animal.getType());
        return currentNumberOfSpecies > 1;
    }

    public List<Animal> findPair(Animal animal, Cell cell) {
        List<Animal> animalListPair = new ArrayList<>();
        if (hasSpaceInCell(animal, cell) && hasAtLeastTwoAnimals(animal, cell)) {
            animalListPair = cell.getAnimalsList()
                    .stream()
                    .filter(animal1 -> animal1.getType().equals(animal.getType()))
                    .limit(2).toList();
        }
        return animalListPair;
    }

    public void reproduce(Animal animal, Cell cell) {
        if (findPair(animal,cell).size() == 2){
            Animal child = factory.create(animal.getType());
            cell.addAnimal(child);
        }
    }

    public void reproduceAll(){
        for (Cell[] row: island.getCells()){
            for (Cell cell: row){
                Set<Animal> reproducedAnimals = new HashSet<>();
                for (Animal animal: new ArrayList<>(cell.getAnimalsList())){
                    List<Animal> pair = findPair(animal, cell);
                    if (!reproducedAnimals.contains(animal) && pair.size() == 2) {
                        reproduce(animal, cell);
                        reproducedAnimals.add(pair.get(0));
                        reproducedAnimals.add(pair.get(1));
                    }
                }
            }
        }
    }

    public void reproducePercent(Animal animal, Cell cell, RandomService randomService) {
        int randomPercent = randomService.randomNumber();
        if (randomPercent <= 49){
            return;
        }
        reproduce(animal, cell);
    }
}
