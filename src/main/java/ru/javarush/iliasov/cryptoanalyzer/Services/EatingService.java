package ru.javarush.iliasov.cryptoanalyzer.Services;

import ru.javarush.iliasov.cryptoanalyzer.Cell;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.Animal;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.AnimalConfig;



import java.util.concurrent.ThreadLocalRandom;

public class EatingService {
    private final Cell cells;

    public EatingService(Cell cells) {
        this.cells = cells;
    }

    public void eat(Animal prey, Animal hunter) {
    int probability = hunter.getConfig().getHuntingProbability(prey.getType());
    int randomPrey = ThreadLocalRandom.current().nextInt(100);
    if (randomPrey < probability){
        cells.getAnimalsList().remove(prey);
        double foodConsumed = prey.getWeight();
        hunter.setWeight(hunter.getWeight() + foodConsumed);
    }
    }
}
