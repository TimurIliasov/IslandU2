package ru.javarush.iliasov.cryptoanalyzer;

import ru.javarush.iliasov.cryptoanalyzer.Organisms.Animal;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.AnimalType;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.Plants;


import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Cell {
    private final List<Animal> animalsList;
    private final List<Plants> plantsList;

    public Cell(List<Animal> animalsList, List<Plants> plantsList) {
        this.animalsList = Objects.requireNonNull(animalsList);
        this.plantsList = Objects.requireNonNull(plantsList);
    }

    public List<Animal> getAnimalsList(){
        return Collections.unmodifiableList(animalsList);
    }

    public List<Plants> getPlantsList(){
        return Collections.unmodifiableList(plantsList);
    }

    public boolean addAnimal(Animal animal){
        if (animal ==null){
            return false;
        }
        return animalsList.add(animal);
    }

    public boolean removeAnimal(Animal animal){
        return animalsList.remove(animal);
    }

    public boolean addPlant(Plants plant){
        if (plant ==null){
            return false;
        }

        return plantsList.add(plant);
    }

    public boolean removePlant(Plants plant){
        return plantsList.remove(plant);
    }

    public long getCurrentAnimalCount(AnimalType type){ return animalsList
            .stream()
            .filter(animal -> animal.getType().equals(type)).count();}
}
