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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkingTest {
    @Mock
    Island island;
    Cell cell;
    AnimalFactory factory;
    @Mock
    MovementService movementService;
    RandomService randomService;
    @Mock
    EatingService eatingService;
    @Mock
    ReproduceService reproduceService;
    @Mock
    DeathService deathService;
    @InjectMocks
    SimulationService simulationService;

    @BeforeEach
    void init(){
        island = new Island(new Cell[5][5]);
        cell = new Cell(new ArrayList<>(), new ArrayList<>());
        factory = new AnimalFactory(new ConfigLoader());
        randomService = mock(RandomService.class);
        movementService = new MovementService(island);
        eatingService = new EatingService(island);
        reproduceService = new ReproduceService(factory, island, randomService);
        deathService = new DeathService();

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
    void whenDeathReallyWorks(){
        cell.addAnimal(factory.create(AnimalType.WOLF));
        cell.addAnimal(factory.create(AnimalType.WOLF));

        Animal rabbit = factory.create(AnimalType.RABBIT);
        cell.addAnimal(rabbit);
        cell.addAnimal(factory.create(AnimalType.RABBIT));
        cell.addAnimal(factory.create(AnimalType.RABBIT));
        cell.addAnimal(factory.create(AnimalType.RABBIT));
        for (int i = 0; i < 4; i++) {
            cell.addPlant(new Plants());
        }

        long currentRabbit = cell.getCurrentAnimalCount(AnimalType.RABBIT);
        when(randomService.randomNumber()).thenReturn(24);

        eatingService.eatAllCells(randomService);
        reproduceService.reproduceAll();

        long afterRabbit = cell.getCurrentAnimalCount(AnimalType.RABBIT);

        assertEquals(currentRabbit - 1, afterRabbit);
    }

    @Test
    void whenSimulationWorks(){
        Animal wolf = factory.create(AnimalType.WOLF);
        Animal rabbit = factory.create(AnimalType.RABBIT);
        Plants plants = new Plants();

        for (int i = 0; i < wolf.getConfig().getMaxInCell(); i++) {
            cell.addAnimal(factory.create(AnimalType.WOLF));
        }
        for (int i = 0; i < rabbit.getConfig().getMaxInCell(); i++) {
            cell.addAnimal(factory.create(AnimalType.RABBIT));
        }
        for (int i = 0; i < plants.getMaxInCell(); i++) {
            cell.addPlant(new Plants());
        }

        when(randomService.randomNumber()).thenReturn(24);

        simulationService.simulateStep();

        for (int i = 0; i < island.getCells().length; i++) {
            for (int j = 0; j < island.getCells()[i].length; j++) {
                Cell currentCell = island.getCells()[i][j];

                if (currentCell.getAnimalsList().contains(wolf)){
                    System.out.println("Wolf located here: [" + i + "] [" + j + "]");
                }
                if (currentCell.getAnimalsList().contains(rabbit)){
                    System.out.println("Rabbit located here: [" + i + "] [" + j + "]");
                }
            }
        }

    }
}
