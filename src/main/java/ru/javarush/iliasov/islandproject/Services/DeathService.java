package ru.javarush.iliasov.islandproject.Services;

import ru.javarush.iliasov.islandproject.Cell;
import ru.javarush.iliasov.islandproject.Organisms.Animal;

public class DeathService {
    public void death(Animal animal, Cell cell){
        if (!animal.getAlive()){
            cell.removeAnimal(animal);
        }
    }
}
