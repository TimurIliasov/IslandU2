
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.javarush.iliasov.islandproject.Island;
import ru.javarush.iliasov.islandproject.Services.*;



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


    @Test
    void shouldStartSimulation() {
        SimulationService simulationService = createSimulationService();
        simulationService.startSimulation();
        assertTrue(simulationService.isSimulationRunning());
        simulationService.stopSimulation();
    }

    @Test
    void shouldNotStartSimulationTwice(){
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
    void shouldRestartSimulationAfterStop(){
        SimulationService service = createSimulationService();
        service.startSimulation();
        service.stopSimulation();
        service.startSimulation();
        assertTrue(service.isSimulationRunning());
        service.stopSimulation();
    }

    @Test
    void shouldExecuteAllSimulationServices()  {
        SimulationService service = createSimulationService();
        service.simulateStep();
//        verify(movementService).moveAll(randomService);
        verify(eatingService).eatAll(randomService);
        verify(eatingService).eatAllPlants();
        verify(reproduceService).reproduceAll();
        verify(statisticsService).getPlantsCount();
        verify(statisticsService).countAnimalsByType();
    }
}
