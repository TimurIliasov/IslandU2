
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.javarush.iliasov.islandproject.Cell;
import ru.javarush.iliasov.islandproject.Island;
import ru.javarush.iliasov.islandproject.Services.*;


import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SimulationTest {
    @Mock
    EatingService eatingService;

    @Mock
    MovementService movementService;

    @Mock
    ReproduceService reproduceService;

    @Mock
    RandomService randomService;

    @Mock
    StatisticsService statisticsService;

    @Mock
    InitializationService initializationService;

    @Mock
    Island island;

    @BeforeEach
    void init(){
        island = new Island(new Cell[2][4]);
        Cell cell = new Cell(new ArrayList<>(), new ArrayList<>());
        for (int i = 0; i < island.getCells().length; i++) {
            for (int j = 0; j < island.getCells()[i].length; j++) {
                island.getCells()[i][j] = cell;
            }
        }
    }


    @Test
    void shouldStartSimulation() {
        SimulationService simulationService = createSimulationService();
        simulationService.startSimulation();
        assertTrue(simulationService.isSimulationRunning());
        simulationService.stopSimulation();
    }

    @Test
    void shouldNotStartSimulationTwice() {
        SimulationService simulationService = createSimulationService();
        simulationService.startSimulation();
        simulationService.startSimulation();
        assertTrue(simulationService.isSimulationRunning());
        simulationService.stopSimulation();

    }

    private SimulationService createSimulationService() {
        return new SimulationService(initializationService, movementService, eatingService, reproduceService, statisticsService, randomService, island);
    }

    @Test
    void shouldStopSimulation() {
        SimulationService simulationService = createSimulationService();
        simulationService.startSimulation();
        assertTrue(simulationService.isSimulationRunning());
        simulationService.stopSimulation();
        assertFalse(simulationService.isSimulationRunning());

    }

    @Test
    void shouldRestartSimulationAfterStop() {
        SimulationService service = createSimulationService();
        service.startSimulation();
        service.stopSimulation();
        service.startSimulation();
        assertTrue(service.isSimulationRunning());
        service.stopSimulation();
    }

    @Test
    void shouldExecuteAllSimulationServices() {
        SimulationService service = createSimulationService();
        service.simulateStep();
        verify(movementService).moveAll(randomService);
        verify(eatingService).eatAllCells(randomService);
        verify(reproduceService).reproduceAll();
        verify(statisticsService).getPlantsCount();
        verify(statisticsService).countAnimalsByType();
    }
}
