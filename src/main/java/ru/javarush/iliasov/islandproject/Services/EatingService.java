package ru.javarush.iliasov.islandproject.Services;

import ru.javarush.iliasov.islandproject.Cell;
import ru.javarush.iliasov.islandproject.Island;
import ru.javarush.iliasov.islandproject.Organisms.Animal;
import ru.javarush.iliasov.islandproject.Organisms.Plants;

import java.util.*;
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


    private Map<Animal, Double> eatInCell(Cell cell, RandomService randomService) {
        List<Animal> animals = cell.getAnimalsList();
        Map<Animal, Double> foodEaten = new LinkedHashMap<>();
        for (Animal hunter : animals) {
            double animalFood = 0;
            for (Animal prey : animals) {
                if (canEat(hunter, prey)
                        && canEatByProbability(prey, hunter, randomService)) {
                    double preyWeight = prey.getWeight();
                    hunter.eat(prey);
                    animalFood += preyWeight;

                    break;
                }
            }
            foodEaten.put(hunter, animalFood);

        }

        return foodEaten;
    }

    public void hunger(Cell cell, RandomService randomService){
        Map<Animal, Double> plantMap = new LinkedHashMap<>(plantsInCell(cell));
        Map<Animal, Double> animalDoubleMap = new LinkedHashMap<>(eatInCell(cell, randomService));


        Map<Animal, Double> totalFoodConsumed = new LinkedHashMap<>(animalDoubleMap);
        plantMap.forEach(((animal, food) ->
                totalFoodConsumed.merge(animal, food, Double::sum)));

        for (Animal animal: cell.getAnimalsList()){
            double totalFood = totalFoodConsumed.getOrDefault(animal, 0.0);

            if (totalFood < animal.getConfig().getFoodNeeded()/8){
                animal.setAlive(false);
            }
        }
    }

    private boolean canEat(Animal hunter, Animal prey) {
        return hunter != prey
                && hunter.getAlive()
                && prey.getAlive();
    }

    private boolean canEatPlants(Animal animal){
        return animal.getConfig().getPlantConsume() > 0;
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

    private Map<Animal, Double> plantsInCell(Cell cell) {
        List<Plants> plantsList = new ArrayList<>(cell.getPlantsList());
        Map<Animal, Double> foodEaten = new LinkedHashMap<>();
        for (Animal animal : new ArrayList<>(cell.getAnimalsList())) {
            if (!canEatPlants(animal) || !animal.getAlive()){
                continue;
            }
            Iterator<Plants> plantsIterator = plantsList.iterator();

            double foodConsumed = 0;

            while (plantsIterator.hasNext() && foodConsumed < animal.getConfig().getFoodNeeded()) {
                    Plants plant = plantsIterator.next();

                    eatPlant(animal, plant, cell);

                    foodConsumed += plant.getWeight();

                    plantsIterator.remove();
            }
            foodEaten.put(animal, foodConsumed);
        }
        return foodEaten;
    }


    public void eatAllCells(RandomService randomService) {
        List<Future<?>> futures = new ArrayList<>();

        for (Cell[] row : island.getCells()) {
            for (Cell cell : row) {
                futures.add(workers.submit(() -> {
                    hunger(cell, randomService);
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

