package ru.javarush.iliasov.cryptoanalyzer.Services;

import ru.javarush.iliasov.cryptoanalyzer.Cell;
import ru.javarush.iliasov.cryptoanalyzer.Island;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.Animal;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.ConfigLoader;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.Plants;

import java.util.ArrayList;

public class InitializationService {
    private final Island island = new Island(new Cell[20][100]);

    public void cellPlant() {
        for (int i = 0; i < island.getCells().length; i++) {
            for (int j = 0; j < island.getCells()[i].length; j++) {
                Cell cell = new Cell(new ArrayList<>(), new ArrayList<>());
                Plants plants = new Plants();

                cell.getPlantsList();
                island.getCells()[i][j] = cell;
            }
        }
    }
}
