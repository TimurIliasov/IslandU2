import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.javarush.iliasov.cryptoanalyzer.Cell;
import ru.javarush.iliasov.cryptoanalyzer.Island;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.AnimalFactory;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.ConfigLoader;
import ru.javarush.iliasov.cryptoanalyzer.Services.*;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimulationTest {
    EatingService eatingService;
    MovementService movementService;
    ReproduceService reproduceService;
    RandomService randomService;
    StatisticsService statisticsService;
    Island island;
    AnimalFactory factory;
    ConfigLoader configLoader;
    Cell cell;

    @BeforeEach
    void init() throws IOException {
        island = new Island(new Cell[5][5]);
        configLoader = new ConfigLoader();
        factory = new AnimalFactory(configLoader);

        movementService = new MovementService(island);
        eatingService = new EatingService(island);
        reproduceService = new ReproduceService(factory, island);
        randomService = new RandomService();
        statisticsService = new StatisticsService(island);

        for (int i = 0; i < island.getCells().length; i++) {
            for (int j = 0; j < island.getCells()[i].length; j++) {
                island.getCells()[i][j] = new Cell(new ArrayList<>(), new ArrayList<>());
            }
        }

        island.getCells()[2][2] = cell;
    }
    @Test
    void whenSimulationStarted(){
        SimulationService simulationService = new SimulationService(movementService, eatingService, reproduceService, statisticsService, randomService);

        simulationService.startSimulation();
        assertTrue(simulationService.isSimulationRunning());

        simulationService.startSimulation();
        assertTrue(simulationService.isSimulationRunning());

        simulationService.stopSimulation();
        assertFalse(simulationService.isSimulationRunning());

        simulationService.startSimulation();
        assertTrue(simulationService.isSimulationRunning());

        simulationService.stopSimulation();
        assertFalse(simulationService.isSimulationRunning());
    }
}
