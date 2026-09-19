package ru.javarush.iliasov.islandproject.Services;

import ru.javarush.iliasov.islandproject.Cell;
import ru.javarush.iliasov.islandproject.Island;
import ru.javarush.iliasov.islandproject.Organisms.Animal;
import ru.javarush.iliasov.islandproject.Organisms.AnimalFactory;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        return animal.getConfig().getMaxInCell() > currentNumberOfSpecies;
    }


    public boolean hasAtLeastTwoAnimals(Animal animal, Cell cell) {
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
                throw new RuntimeException(e);
            }
        }
    }

    public void reproduceCell(Cell cell) {
        synchronized (cell) {
            Set<Animal> reproducedAnimals = new HashSet<>();
            for (Animal animal : cell.getAnimalsList()) {
                List<Animal> pair = findPair(animal, cell);
                if (pair.size() == 2
                        && !reproducedAnimals.contains(pair.get(1))
                        && !reproducedAnimals.contains(pair.get(0))
                        && reproducePercent()) {
                    reproduce(animal, cell);
                    reproducedAnimals.add(pair.get(0));
                    reproducedAnimals.add(pair.get(1));
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

    public boolean isShutdown(){
        return workers.isShutdown();
    }
}
