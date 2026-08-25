package ru.javarush.iliasov.cryptoanalyzer.Services;

import ru.javarush.iliasov.cryptoanalyzer.Cell;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.Animal;

public class DeathService {
    public void death(Animal animal, Cell cell){
        if (!animal.getAlive()){
            cell.removeAnimal(animal);
        }
    }
}
