package ru.javarush.iliasov.cryptoanalyzer.Services;

import ru.javarush.iliasov.cryptoanalyzer.Cell;
import ru.javarush.iliasov.cryptoanalyzer.Island;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.Animal;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class MovementService {
    private final Island island;

    public MovementService(Island island) {
        this.island = island;
    }


    public void move() {
        Map<Animal, Movement> movementMap = new HashMap<>();
        for (int i = 0; i < island.getCells().length; i++) {
            for (int j = 0; j < island.getCells()[i].length; j++) {
                Cell cell = island.getCells()[i][j];
                for (Animal animal : cell.getAnimalsList()) {
                    movementMap.put(animal, createMovement(animal, i, j));
                }

            }
        }
        for (Map.Entry<Animal, Movement> entry : movementMap.entrySet()) {
            Movement movement = entry.getValue();
            movement.oldCell().removeAnimal(entry.getKey());
            movement.newCell().addAnimal(entry.getKey());
        }
    }

    private Movement createMovement(Animal animal, int i, int j) {
        int distance = ThreadLocalRandom.current().nextInt(1, animal.getConfig().getSpeed() + 1);
        int direction = ThreadLocalRandom.current().nextInt(0, 4);

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

        return new Movement(
                island.getCells()[i][j],
                island.getCells()[newI][newJ]
        );
    }
}


