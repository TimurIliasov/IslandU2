package ru.javarush.iliasov.islandproject;

import ru.javarush.iliasov.islandproject.Organisms.Animal;
import ru.javarush.iliasov.islandproject.Organisms.AnimalType;
import ru.javarush.iliasov.islandproject.Organisms.Plants;


import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Cell {
    private final List<Animal> animalsList;
    private final List<Plants> plantsList;

    public Cell(List<Animal> animalsList, List<Plants> plantsList) {
        this.animalsList = Collections.synchronizedList(Objects.requireNonNull(animalsList));
        this.plantsList = Collections.synchronizedList(Objects.requireNonNull(plantsList));
    }

    public List<Animal> getAnimalsList() {
        synchronized (animalsList) {
            return List.copyOf(animalsList);
        }
    }

    public List<Plants> getPlantsList() {
        synchronized (plantsList) {
            return List.copyOf(plantsList);
        }
    }

    public boolean addAnimal(Animal animal) {
        if (animal == null) {
            return false;
        }
        return animalsList.add(animal);
    }

    public boolean removeAnimal(Animal animal) {
        return animalsList.remove(animal);
    }

    public boolean addPlant(Plants plant) {
        if (plant == null) {
            return false;
        }

        return plantsList.add(plant);
    }

    public boolean removePlant(Plants plant) {
        return plantsList.remove(plant);
    }

    public long getCurrentAnimalCount(AnimalType type) {
        synchronized (animalsList) {
            return animalsList
                    .stream()
                    .filter(animal -> animal.getType() == type).count();
        }
    }

    public int getCurrentPlantsCount() {
        synchronized (plantsList) {
            return plantsList.size();
        }
    }
}
