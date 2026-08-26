package ru.javarush.iliasov.cryptoanalyzer.Services;

import ru.javarush.iliasov.cryptoanalyzer.Cell;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.Animal;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.AnimalConfig;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.Plants;


import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class EatingService {
    private final Cell cells;

    public EatingService(Cell cells) {
        this.cells = cells;
    }

    public void eat(Animal prey, Animal hunter, RandomService randomService) {
        int probability = hunter.getConfig().getHuntingProbability(prey.getType());
        int randomPrey = randomService.randomNumber();
        if (randomPrey < probability) {
            hunter.eat(prey);
        }
    }

    public void eatPlant(Animal animal, Plants plants){
        animal.eatPlant(plants);
        cells.removePlant(plants);
    }
}
