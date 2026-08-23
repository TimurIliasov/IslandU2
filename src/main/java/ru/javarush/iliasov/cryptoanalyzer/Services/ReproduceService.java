package ru.javarush.iliasov.cryptoanalyzer.Services;

import ru.javarush.iliasov.cryptoanalyzer.Cell;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.Animal;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.AnimalFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ReproduceService {
    private final AnimalFactory factory;

    public ReproduceService(AnimalFactory factory) {
        this.factory = factory;
    }

    public boolean hasSpaceInCell(Animal animal, Cell cell){
        long currentNumberOfSpecies = cell.getCurrentAnimalCount(animal.getType());
        return animal.getConfig().getMaxInCell() > currentNumberOfSpecies;
    }

    public boolean hasAtLeastTwoAnimals(Animal animal, Cell cell){
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

    public void reproduce(Animal animal, Cell cell) throws IOException {
        if (findPair(animal,cell).size() == 2){
            Animal child = factory.create(animal.getType());
            cell.addAnimal(child);
        }
    }

    public void reproducePercent(Animal animal, Cell cell) throws IOException {
        int randomPercent = ThreadLocalRandom.current().nextInt(100 );
        if (randomPercent <= 49){
            return;
        }
        reproduce(animal, cell);
    }
}
