package ru.javarush.iliasov.cryptoanalyzer;

import ru.javarush.iliasov.cryptoanalyzer.Organisms.Animals;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.Plants;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Cell {
    private final List<Animals> animalsList;
    private final List<Plants> plantsList;

    public Cell(List<Animals> animalsList, List<Plants> plantsList) {
        this.animalsList = Objects.requireNonNull(animalsList);
        this.plantsList = Objects.requireNonNull(plantsList);
    }

    public List<Animals> getAnimalsList(){
        return Collections.unmodifiableList(animalsList);
    }

    public List<Plants> getPlantsList(){
        return Collections.unmodifiableList(plantsList);
    }

    public boolean addAnimal(Animals animal){
        if (animal ==null){
            return false;
        }
        return animalsList.add(animal);
    }

    public boolean removeAnimal(Animals animal){
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
}
