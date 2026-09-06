package ru.javarush.iliasov.islandproject.Services;

import ru.javarush.iliasov.islandproject.Cell;
import ru.javarush.iliasov.islandproject.Organisms.Animal;

public record Movement(Animal animal, Cell oldCell, Cell newCell) {


}

