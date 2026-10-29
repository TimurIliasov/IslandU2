import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

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
public class NewTest {
    Island island;
    Cell cell = new Cell(new ArrayList<>(), new ArrayList<>());
    AnimalFactory factory = new AnimalFactory(new ConfigLoader());
    Animal wolf;
    Animal rabbit;
    EatingService eatingService;
    @Mock
    MovementService movementService;
    ReproduceService reproduceService;
    @Mock
    RandomService randomService;


    @BeforeAll
    static void setup(){
        System.out.println("Testing services: ");
    }

    @BeforeEach
    void init(){
        island = new Island(new Cell[5][5]);
        wolf = factory.create(AnimalType.WOLF);
        rabbit = factory.create(AnimalType.RABBIT);

        eatingService = spy(new EatingService(island));
        movementService = spy(new MovementService(island));
        reproduceService = spy(new ReproduceService(factory, island, randomService));

        for (int i = 0; i < island.getCells().length; i++) {
            for (int j = 0; j < island.getCells()[i].length; j++) {
                Cell currentCell = new Cell(new ArrayList<>(), new ArrayList<>());
                island.getCells()[i][j] = currentCell;
            }
        }

        cell = island.getCells()[1][1];


    }



    @Test
    void whenReproduceWorksAfterMovement(){
        when(randomService.getRandomNumber(wolf.getConfig().getSpeed())).thenReturn(1);
        when(randomService.getRandomDirection(3)).thenReturn(1);

        Animal wolfPapa = factory.create(AnimalType.WOLF);
        Animal wolfMama = factory.create(AnimalType.WOLF);

        cell.addAnimal(wolfPapa);
        cell.addAnimal(wolfMama);

        long wolfBefore = cell.getCurrentAnimalCount(AnimalType.WOLF);

        movementService.moveAll(randomService);
        eatingService.eatAllCells(randomService);
        reproduceService.reproduceAll();

        for (int i = 0; i < island.getCells().length; i++) {
            for (int j = 0; j < island.getCells()[i].length; j++) {
                Cell currentCell = island.getCells()[i][j];

                if (currentCell.getAnimalsList().contains(wolf)){
                    System.out.println("Wolf located here: [" + i + "] ["  + j + "]");
                }
            }
        }
    }

    @Test
    void testRandom(){
        when(randomService.getRandomNumber(10)).thenReturn(2, 3, 5);

        int result = randomService.getRandomNumber(10);
        int result1 = randomService.getRandomNumber(10);
        int result2 = randomService.getRandomNumber(10);
        int result3 = randomService.getRandomNumber(10);
        System.out.println(result);
        System.out.println(result1);
        System.out.println(result2);
        System.out.println(result3);
        assertEquals(2, result);
    }

    @Test
    void whenReproduceWorks(){
        Animal wolf1 = factory.create(AnimalType.WOLF);
        Animal wolf2 = factory.create(AnimalType.WOLF);

        cell.addAnimal(wolf1);
        cell.addAnimal(wolf2);

        when(randomService.randomNumber()).thenReturn(24);

        long wolvesBefore = cell.getCurrentAnimalCount(AnimalType.WOLF);

        reproduceService.reproduceAll();

        long wolvesAfter = cell.getCurrentAnimalCount(AnimalType.WOLF);

        assertEquals(wolvesBefore + 1, wolvesAfter);
        assertTrue(cell.getAnimalsList().contains(wolf1));
        assertTrue(cell.getAnimalsList().contains(wolf2));
    }

    @AfterEach
    void tear(){
        eatingService.shutdown();
        reproduceService.shutdown();
        movementService.shutdown();
    }

    @AfterAll
    static void tearDown(){
        System.out.println("Testing completed");
    }
}
