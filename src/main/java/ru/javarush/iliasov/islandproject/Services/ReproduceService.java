package ru.javarush.iliasov.islandproject.Services;

import ru.javarush.iliasov.islandproject.Cell;
import ru.javarush.iliasov.islandproject.Exceptions.MaxInCellException;
import ru.javarush.iliasov.islandproject.Island;
import ru.javarush.iliasov.islandproject.Organisms.Animal;
import ru.javarush.iliasov.islandproject.Organisms.AnimalFactory;


import java.util.*;
import java.util.concurrent.*;


public class ReproduceService {
    private final AnimalFactory factory;
    private final Island island;
    private final RandomService randomService;

    private final ExecutorService workers = Executors.newFixedThreadPool(4);

    public ReproduceService(AnimalFactory factory, Island island, RandomService randomService) {
        this.factory = factory;
        this.island = island;
        this.randomService = randomService;
    }

    public boolean hasSpaceInCell(Animal animal, Cell cell) {
        long currentNumberOfSpecies = cell.getCurrentAnimalCount(animal.getType());
        if (currentNumberOfSpecies > animal.getConfig().getMaxInCell()) {
            throw new MaxInCellException("Animals in cell is overflowed!");
        }
        return animal.getConfig().getMaxInCell() > currentNumberOfSpecies;
    }


    public boolean hasAtLeastTwoAnimals(Animal animal, Cell cell) {
        long currentNumberOfSpecies = cell.getCurrentAnimalCount(animal.getType());
        return currentNumberOfSpecies > 1;
    }

    public Animal findPair(Animal animal, List<Animal> animalList, Set<Animal> reproducedAnimals) {
        Animal pair = null;
        for (Animal candidate : animalList) {
            if (candidate != animal && candidate.getType() == animal.getType() && !reproducedAnimals.contains(candidate)) {
                return candidate;
            }
            pair = candidate;
        }
        return pair;
    }

    public void reproduce(Animal animal, Cell cell) {
        Animal child = factory.create(animal.getType());
        cell.addAnimal(child);
    }

    public void reproduceAll() {
        List<Future<?>> futures = new ArrayList<>();

        for (Cell[] row : island.getCells()) {
            for (Cell cell : row) {
                futures.add(workers.submit(() -> reproduceCell(cell)));
            }
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof MaxInCellException maxInCellException) {
                    throw maxInCellException;
                }
                throw new RuntimeException(e);
            }
        }
    }

    public void reproduceCell(Cell cell) {
        synchronized (cell) {
            List<Animal> animalList = new ArrayList<>(cell.getAnimalsList());
            Set<Animal> reproducedAnimals = Collections.newSetFromMap(new IdentityHashMap<>());
            for (Animal animal : animalList) {
                if (reproducedAnimals.contains(animal)
                        || !hasSpaceInCell(animal, cell)
                        || !hasAtLeastTwoAnimals(animal, cell)) {
                    continue;
                }

                Animal partner = findPair(animal, animalList, reproducedAnimals);
                if (partner == null){
                    continue;
                }

                reproducedAnimals.add(animal);
                reproducedAnimals.add(partner);

                if (reproducePercent()) {
                    reproduce(animal, cell);
                }
            }
        }
    }


    public boolean reproducePercent() {
        return randomService.randomNumber() < 50;
    }

    public void shutdown() {
        workers.shutdown();

        try {
            if (!workers.awaitTermination(5, TimeUnit.SECONDS)) {
                workers.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public boolean isShutdown() {
        return workers.isShutdown();
    }
}
