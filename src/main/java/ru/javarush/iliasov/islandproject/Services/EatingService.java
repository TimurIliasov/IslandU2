package ru.javarush.iliasov.islandproject.Services;

import ru.javarush.iliasov.islandproject.Cell;
import ru.javarush.iliasov.islandproject.Island;
import ru.javarush.iliasov.islandproject.Organisms.Animal;
import ru.javarush.iliasov.islandproject.Organisms.Plants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

public class EatingService {
    private final Island island;
    private final DeathService deathService = new DeathService();

    private final ExecutorService workers = Executors.newFixedThreadPool(4);

    public EatingService(Island island) {
        this.island = island;
    }

    private boolean canEatByProbability(Animal prey, Animal hunter, RandomService randomService) {
        int probability = hunter.getConfig().getHuntingProbability(prey.getType());
        int randomChance = randomService.randomNumber();
        return randomChance < probability;
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

    private void plantsInCell(Cell cell) {
        List<Plants> plantsList = new ArrayList<>(cell.getPlantsList());
        for (Animal animal : new ArrayList<>(cell.getAnimalsList())) {
            Iterator<Plants> plantsIterator = plantsList.iterator();

            double foodEaten = 0;

            while (plantsIterator.hasNext() && foodEaten < animal.getConfig().getFoodNeeded()) {
                Plants plant = plantsIterator.next();

                eatPlant(animal, plant, cell);

                foodEaten += plant.getWeight();

                plantsIterator.remove();
            }
        }
    }

    public void eatAllCells(RandomService randomService) {
        List<Future<?>> futures = new ArrayList<>();

        for (Cell[] row : island.getCells()) {
            for (Cell cell : row) {
                futures.add(workers.submit(() -> {
                    eatInCell(cell, randomService);
                    plantsInCell(cell);
                    removeDeadAnimals(cell);
                }));
            }
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void shutdown(){
        workers.shutdown();

        try {
            if (!workers.awaitTermination(5, TimeUnit.SECONDS)){
                workers.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}

