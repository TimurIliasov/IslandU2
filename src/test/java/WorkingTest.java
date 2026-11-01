import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.javarush.iliasov.islandproject.Cell;
import ru.javarush.iliasov.islandproject.Island;
import ru.javarush.iliasov.islandproject.Organisms.*;
import ru.javarush.iliasov.islandproject.Services.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkingTest {
    Island island;
    Cell cell;
    AnimalFactory factory;
    MovementService movementService;
    RandomService randomService;
    EatingService eatingService;
    ReproduceService reproduceService;
    DeathService deathService;
    SimulationService simulationService;

    @BeforeEach
    void init(){
        island = new Island(new Cell[5][5]);
        cell = new Cell(new ArrayList<>(), new ArrayList<>());
        factory = new AnimalFactory(new ConfigLoader());
        randomService = mock(RandomService.class);
        movementService = spy(new MovementService(island));
        eatingService = spy(new EatingService(island));
        reproduceService = spy(new ReproduceService(factory, island, randomService));
        deathService = new DeathService();
        simulationService = spy(new SimulationService(new InitializationService(randomService, new ConfigLoader(), factory), movementService,
                eatingService,
                reproduceService,
                new StatisticsService(island),
                randomService,
                island));

        for (int i = 0; i < island.getCells().length; i++) {
            for (int j = 0; j < island.getCells()[i].length; j++) {
                Cell currentCell = new Cell(new ArrayList<>(), new ArrayList<>());
                island.getCells()[i][j] = currentCell;
            }
        }

        cell = island.getCells()[1][1];
    }

    @AfterEach
    void tear(){
        movementService.shutdown();
        eatingService.shutdown();
        reproduceService.shutdown();
    }

    @RepeatedTest(5)
    void whenReproduceWorksAfterMovement(){
        Animal wolfPapa = factory.create(AnimalType.WOLF);
        Animal wolfMama = factory.create(AnimalType.WOLF);
        Animal rabbit1 = factory.create(AnimalType.RABBIT);
        Animal rabbit2 = factory.create(AnimalType.RABBIT);

        cell.addAnimal(wolfPapa);
        cell.addAnimal(wolfMama);
        cell.addAnimal(rabbit1);
        cell.addAnimal(rabbit2);
        cell.addPlant(new Plants());
        cell.addPlant(new Plants());


        long currentWolves = cell.getCurrentAnimalCount(AnimalType.WOLF);
        System.out.println(currentWolves);

        when(randomService.getRandomNumber(wolfMama.getConfig().getSpeed())).thenReturn(1);
        when(randomService.getRandomDirection(3)).thenReturn(1);
        when(randomService.getRandomNumber(rabbit1.getConfig().getSpeed())).thenReturn(1);
        Cell currentCell = new Cell(new ArrayList<>(), new ArrayList<>());

        island.getCells()[2][1] = currentCell;


        movementService.moveAll(randomService);
        eatingService.eatAllCells(randomService);
        reproduceService.reproduceAll();



        long wolvesAfter = currentCell.getCurrentAnimalCount(AnimalType.WOLF);
        System.out.println(wolvesAfter);

        assertTrue(currentCell.getAnimalsList().contains(wolfMama));
        assertEquals(currentWolves + 1, wolvesAfter);
    }



    @Test
    void whenSimulationWorks(){
        Animal wolf = factory.create(AnimalType.WOLF);
        Animal rabbit = factory.create(AnimalType.RABBIT);

        for (int i = 0; i < wolf.getConfig().getMaxInCell() - 1; i++) {
            cell.addAnimal(factory.create(AnimalType.WOLF));
        }
        for (int i = 0; i < rabbit.getConfig().getMaxInCell(); i++) {
            cell.addAnimal(factory.create(AnimalType.RABBIT));
        }
        for (int i = 0; i < Plants.getMaxInCell(); i++) {
            cell.addPlant(new Plants());
        }

        simulationService.simulateStep();

        verify(movementService).moveAll(randomService);
        verify(eatingService).eatAllCells(randomService);
        verify(reproduceService).reproduceAll();
    }

    @Test
    void whenSimulationFeedsAndReproduceAnimals(){
        for (int i = 0; i < 25; i++) {
            cell.addAnimal(factory.create(AnimalType.WOLF));
            cell.addAnimal(factory.create(AnimalType.RABBIT));
        }

        when(randomService.getRandomNumber(200)).thenReturn(200);

        long currentWolves = cell.getCurrentAnimalCount(AnimalType.WOLF);
        long currentRabbits = cell.getCurrentAnimalCount(AnimalType.RABBIT);
        long currentPlants = cell.getCurrentPlantsCount();

        simulationService.simulateStep();

        long wolvesAfter = cell.getCurrentAnimalCount(AnimalType.WOLF);
        long rabbitsAfter = cell.getCurrentAnimalCount(AnimalType.RABBIT);
        long plantsAfter = cell.getCurrentPlantsCount();


        assertEquals(currentWolves + 1, wolvesAfter);
        assertEquals(currentRabbits - 25, rabbitsAfter);
        assertEquals(currentPlants + 174, plantsAfter);

    }

    @Test
    void shouldCreateOneOffspringWhenFourWolvesAreInCell(){
        for (int i = 0; i < 4; i++) {
            cell.addAnimal(factory.create(AnimalType.WOLF));
        }

        long currentWolves = cell.getCurrentAnimalCount(AnimalType.WOLF);

        reproduceService.reproduceAll();

        long wolvesAfter = cell.getCurrentAnimalCount(AnimalType.WOLF);

        assertEquals(currentWolves + 1, wolvesAfter);
    }

    @Test
    void whenEatingKillsEveryPrey(){
        for (int i = 0; i < 30; i++) {
            cell.addAnimal(factory.create(AnimalType.WOLF));
            cell.addAnimal(factory.create(AnimalType.RABBIT));
            cell.addPlant(new Plants());
        }
        long currentRabbits = cell.getCurrentAnimalCount(AnimalType.RABBIT);
        long currentWolves = cell.getCurrentAnimalCount(AnimalType.WOLF);

        when(randomService.randomNumber()).thenReturn(24);

        eatingService.eatAllCells(randomService);

        long rabbitsAfter = cell.getCurrentAnimalCount(AnimalType.RABBIT);
        long wolvesAfter = cell.getCurrentAnimalCount(AnimalType.WOLF);


        assertEquals(currentWolves, wolvesAfter);
        assertEquals(currentRabbits - 30, rabbitsAfter, "All rabbits must die");
    }

    @Test
    void whenOneWolfDoesNotReproduce(){
        cell.addAnimal(factory.create(AnimalType.WOLF));

        long currentWolf = cell.getCurrentAnimalCount(AnimalType.WOLF);

        reproduceService.reproduceAll();

        long wolfAfter = cell.getCurrentAnimalCount(AnimalType.WOLF);

        assertEquals(currentWolf, wolfAfter);
    }

    @Test
    void whenMaxLimitDoNotReproduce(){
        Animal wolf = factory.create(AnimalType.WOLF);

        for (int i = 0; i < wolf.getConfig().getMaxInCell(); i++) {
            cell.addAnimal(factory.create(AnimalType.WOLF));
        }
        long currentWolves = cell.getCurrentAnimalCount(AnimalType.WOLF);

        reproduceService.reproduceAll();

        long wolvesAfter = cell.getCurrentAnimalCount(AnimalType.WOLF);

        assertEquals(currentWolves, wolvesAfter);
    }
}
