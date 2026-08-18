package ru.javarush.iliasov.cryptoanalyzer.Services;

import ru.javarush.iliasov.cryptoanalyzer.Cell;
import ru.javarush.iliasov.cryptoanalyzer.Island;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.Animal;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class MovementServices {
    private final Island island;

    public MovementServices(Island island) {
        this.island = island;
    }


    public void move() {
        Map<Animal, Movement> movementMap = new HashMap<>();
        for (int i = 0; i < island.getCells().length; i++) {
            for (int j = 0; j < island.getCells()[i].length; j++) {
                Cell cell = island.getCells()[i][j];
                List<Animal> animals = cell.getAnimalsList();
                for (Animal animal : animals) {
                    int newI = i;
                    int newJ = j;
                    int speed = animal.getConfig().getSpeed();
                    int random = ThreadLocalRandom.current().nextInt(0, speed + 1);
                    int direction = ThreadLocalRandom.current().nextInt(0, 4);
                    if (direction == 0) {
                        newI = i - random;
                    }
                    if (direction == 1) {
                        newI = i + random;
                    }
                    if (direction == 2) {
                        newJ = j - random;
                    }
                    if (direction == 3) {
                        newJ = j + random;
                    }

                    if (newI < 0
                            || newI >= island.getCells().length
                            || newJ < 0
                            || newJ >= island.getCells()[i].length) {
                        newI = i;
                        newJ = j;
                    }
                    Movement movement = new Movement(island.getCells()[i][j], island.getCells()[newI][newJ]);
                    movementMap.put(animal, movement);
                }
            }
        }

        for (Animal animal: movementMap.keySet()){
            Movement movement = movementMap.get(animal);
            Cell oldCell = movement.oldCell();
            Cell newCell = movement.newCell();

            oldCell.removeAnimal(animal);
            newCell.addAnimal(animal);

        }
    }
}

