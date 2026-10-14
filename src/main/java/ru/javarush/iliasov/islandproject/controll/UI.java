package ru.javarush.iliasov.islandproject.controll;

import org.fusesource.jansi.AnsiConsole;
import ru.javarush.iliasov.islandproject.Cell;
import ru.javarush.iliasov.islandproject.Island;
import ru.javarush.iliasov.islandproject.Organisms.Animal;
import ru.javarush.iliasov.islandproject.Organisms.AnimalType;
import ru.javarush.iliasov.islandproject.Services.SimulationService;

import java.util.*;

import static org.fusesource.jansi.Ansi.ansi;


public class UI {
    private final SimulationService simulationService;
    private final Island island;

    public UI(SimulationService simulationService, Island island) {
        this.simulationService = simulationService;
        this.island = island;
    }


    public void start() {
        System.out.println("Island Simulation: ");

        System.out.println("Start");
        simulationService.startIsland();
        simulationService.startSimulation();

        while (simulationService.isSimulationRunning()) {
            refreshConsole();
            displayIsland();

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }

        simulationService.shutdownReproduceService();

    }

    public void displayIsland() {
        for (Cell[] row : island.getCells()) {
            for (Cell currentCell : row) {
                displayCell(currentCell);
                System.out.println();
            }
            System.out.println();
        }
        System.out.println("Total: ");
    }

    public void displayCell(Cell cell) {
        String text = cell.toString();
        System.out.printf("%-80s", text);
    }

    public void refreshConsole() {
        AnsiConsole.out().print(ansi().eraseScreen().cursor(1, 1));
        AnsiConsole.out().flush();
    }
}
