package ru.javarush.iliasov.islandproject;

import org.fusesource.jansi.AnsiConsole;
import ru.javarush.iliasov.islandproject.Organisms.AnimalFactory;
import ru.javarush.iliasov.islandproject.Organisms.ConfigLoader;
import ru.javarush.iliasov.islandproject.Services.*;
import ru.javarush.iliasov.islandproject.controll.UI;

import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {
        ConfigLoader configLoader = new ConfigLoader();
        AnimalFactory factory = new AnimalFactory(configLoader);

        Island island;
        island = new Island(new Cell[2][2]);

        RandomService randomService = new RandomService();
        InitializationService initializationService = new InitializationService(randomService, configLoader, factory);
        MovementService movementService = new MovementService(island);
        EatingService eatingService = new EatingService(island);
        ReproduceService reproduceService = new ReproduceService(factory, island, randomService);
        StatisticsService statisticsService = new StatisticsService(island);


        AnsiConsole.systemInstall();
        SimulationService simulationService = new SimulationService(initializationService, movementService, eatingService, reproduceService, statisticsService, randomService, island);
        UI ui = new UI(simulationService, island);
        ui.start();
    }
}