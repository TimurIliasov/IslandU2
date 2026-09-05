package ru.javarush.iliasov.islandproject.Services;

import ru.javarush.iliasov.islandproject.Cell;
import ru.javarush.iliasov.islandproject.Island;
import ru.javarush.iliasov.islandproject.Organisms.Animal;
import ru.javarush.iliasov.islandproject.Organisms.Plants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EatingService {
    private final Island island;
    private final DeathService deathService = new DeathService();

    public EatingService(Island island) {
        this.island = island;
    }

    private boolean canEatByProbability(Animal prey, Animal hunter, RandomService randomService) {
        int probability = hunter.getConfig().getHuntingProbability(prey.getType());
        int randomChance = randomService.randomNumber();
        return randomChance < probability;
    }

    public void eatAll(RandomService randomService) {
        for (Cell[] row : island.getCells()) {
            for (Cell cell : row) {
                eatInCell(cell, randomService);
                removeDeadAnimals(cell);
            }
        }
    }

    private void eatInCell(Cell cell, RandomService randomService) {
        List<Animal> animals = cell.getAnimalsList();
        for (Animal hunter : animals) {
            for (Animal prey : animals) {
                if (canEat(hunter, prey) && canEatByProbability(prey, hunter, randomService)) {
                    hunter.eat(prey);
                }
            }
        }
    }

    private boolean canEat(Animal hunter, Animal prey) {
        return hunter != prey
                && hunter.getAlive()
                && prey.getAlive();
    }

    private void removeDeadAnimals(Cell cell) {
        for (Animal animal : new ArrayList<>(cell.getAnimalsList())) {
            if (!animal.getAlive()) {
                deathService.death(animal, cell);
            }
        }
    }

    public void eatPlant(Animal animal, Plants plant, Cell cell) {
        animal.eatPlant(plant);
        cell.removePlant(plant);
    }

    public void eatAllPlants() {
        for (Cell[] row : island.getCells()) {
            for (Cell cell : row) {
                plantsInCell(cell);
            }
        }
    }

    private void plantsInCell(Cell cell) {
        List<Plants> plantsList = new ArrayList<>(cell.getPlantsList());
        for (Animal animal : new ArrayList<>(cell.getAnimalsList())) {
            Iterator<Plants> plantsIterator = plantsList.iterator();
            while (plantsIterator.hasNext() && animal.getWeight() < animal.getConfig().getFoodNeeded()) {
                Plants plant = plantsIterator.next();
                eatPlant(animal, plant, cell);
                plantsIterator.remove();
            }
        }
    }
}

