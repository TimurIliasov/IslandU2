package ru.javarush.iliasov.islandproject.Services;

import ru.javarush.iliasov.islandproject.Cell;
import ru.javarush.iliasov.islandproject.Island;
import ru.javarush.iliasov.islandproject.Organisms.Animal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MovementService {
    private final Island island;

    private final ExecutorService workers = Executors.newFixedThreadPool(4);

    public MovementService(Island island) {
        this.island = island;
    }

    public boolean movementAllowed(Cell cell, Animal animal){
        long currentAnimalCount = cell.getCurrentAnimalCount(animal.getType());
        return currentAnimalCount < animal.getConfig().getMaxInCell();
    }


    public Movement createMovement(Animal animal, int i, int j, RandomService randomService) {
        int distance = randomService.getRandomNumber(animal.getConfig().getSpeed());
        int direction = randomService.getRandomNumber(3);
        int newI = i;
        int newJ = j;

        switch (direction) {
            case 0 -> newI -= distance;
            case 1 -> newI += distance;
            case 2 -> newJ -= distance;
            case 3 -> newJ += distance;
        }

        if (newI < 0
                || newI >= island.getCells().length
                || newJ < 0
                || newJ >= island.getCells()[i].length) {
            newI = i;
            newJ = j;
        }

        Cell targetCell = island.getCells()[newI][newJ];
        if (!movementAllowed(targetCell, animal)){
            newI = i;
            newJ = j;
        }

        return new Movement(animal,
                island.getCells()[i][j],
                island.getCells()[newI][newJ]
        );
    }

    public void moveAll(RandomService randomService) {
        List<Future<List<Movement>>> futures = new ArrayList<>();
        List<Movement> movements = new ArrayList<>();
        for (int i = 0; i < island.getCells().length; i++) {
            final int rowIndex = i;
            for (int j = 0; j < island.getCells()[i].length; j++) {
                final int columnIndex = j;
                Cell cell = island.getCells()[i][j];
                Callable<List<Movement>> callable = () -> moveInCell(cell, rowIndex, columnIndex, randomService);
                futures.add(workers.submit(callable));
            }
        }
        for (Future<List<Movement>> future : futures) {
            try {
                movements.addAll(future.get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        for (Movement movement : movements) {
            if (movement.newCell().getCurrentAnimalCount(movement.animal().getType()) >= movement.animal().getConfig().getMaxInCell()){
                continue;
            }
            movement.oldCell().removeAnimal(movement.animal());
            movement.newCell().addAnimal(movement.animal());
        }
    }

    public List<Movement> moveInCell(Cell cell, int i, int j, RandomService randomService) {
        List<Movement> movementsList = new ArrayList<>();

        for (Animal animal : cell.getAnimalsList()) {
            movementsList.add(createMovement(animal, i, j, randomService));
        }

        return movementsList;
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


