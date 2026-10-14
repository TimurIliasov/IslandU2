package ru.javarush.iliasov.islandproject;

import ru.javarush.iliasov.islandproject.Organisms.Animal;
import ru.javarush.iliasov.islandproject.Organisms.AnimalType;
import ru.javarush.iliasov.islandproject.Organisms.Plants;


import java.util.*;

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

    @Override
    public String toString() {
        List<Animal> animals = new ArrayList<>();
        animals.addAll(animalsList);
        Map<AnimalType, Long> animalCounts = new LinkedHashMap<>();
        long animalCount = 0;
        StringBuilder stringBuilder = new StringBuilder();

        for (Animal animal: animals){
            animalCount = getCurrentAnimalCount(animal.getType());
            animalCounts.put(animal.getType(), animalCount);
        }

        for (Map.Entry<AnimalType, Long> map: animalCounts.entrySet()){
            stringBuilder.append("| ");
            stringBuilder.append(map.getKey());
            stringBuilder.append(": ");
            stringBuilder.append(map.getValue());
        }

        long plantCount = getCurrentPlantsCount();

        stringBuilder.append("| Plants: ");
        stringBuilder.append(plantCount);
        stringBuilder.append(" |");
        return stringBuilder.toString();
    }
}
