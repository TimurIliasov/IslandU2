import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.javarush.iliasov.islandproject.Cell;
import ru.javarush.iliasov.islandproject.Island;
import ru.javarush.iliasov.islandproject.Organisms.*;
import ru.javarush.iliasov.islandproject.Services.Movement;
import ru.javarush.iliasov.islandproject.Services.MovementService;
import ru.javarush.iliasov.islandproject.Services.RandomService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.javarush.iliasov.islandproject.Organisms.AnimalType.WOLF;

@ExtendWith(MockitoExtension.class)
class MovementTest {
    Island island;
    Cell cell;
    AnimalFactory factory;
    Animal animal;
    @Mock
    private RandomService randomService;
    Movement movement;
    @Mock
    MovementService movementService;

    @BeforeEach
    void init() throws IOException {
        factory = new AnimalFactory(new ConfigLoader());
        island = new Island(new Cell[5][5]);
        animal = factory.create(WOLF);
        movementService = spy(new MovementService(island));
        for (int i = 0; i < island.getCells().length; i++) {
            for (int j = 0; j < island.getCells()[i].length; j++) {
                Cell currentCell = new Cell(new ArrayList<>(), new ArrayList<>());
                island.getCells()[i][j] = currentCell;
            }
        }


        cell = island.getCells()[0][0];
    }

    @AfterEach
    void tearDown() {
        movementService.shutdown();
    }

    @Test
    void testingMovementServiceReallyWorks() {

        when(randomService.getRandomNumber(animal.getConfig().getSpeed())).thenReturn(1);
        when(randomService.getRandomDirection(3)).thenReturn(2);
        island.getCells()[2][2].addAnimal(animal);
        System.out.println(animal.getConfig().getSpeed());
        System.out.println(Mockito.mockingDetails(randomService).isMock());
        movementService.createMovement(animal, 1, 2, randomService);

        movementService.moveAll(randomService);

        verify(movementService).createMovement(animal, 1, 2, randomService);

        assertFalse(island.getCells()[2][2].getAnimalsList()
                .contains(animal));


        boolean foundMovement = false;
        for (int i = 0; i < island.getCells().length; i++) {
            for (int j = 0; j < island.getCells()[i].length; j++) {
                if (i == 2 && j == 2) {
                    continue;
                }
                if (island.getCells()[i][j].getAnimalsList().contains(animal)) {
                    foundMovement = true;
                }
            }
            if (foundMovement) {
                break;
            }
        }
        assertTrue(foundMovement);
    }

    @Test
    void whenMoveAll_thenAllAnimalsStillExists() {
        long animalCount = countAnimal(animal.getType());
        movementService.moveAll(randomService);
        long animalCountAfterMove = countAnimal(animal.getType());
        assertEquals(animalCount, animalCountAfterMove);
    }

    private long countAnimal(AnimalType type) {
        long count = 0;

        for (Cell[] row : island.getCells()) {
            for (Cell column : row) {
                count += column.getCurrentAnimalCount(type);
            }
        }
        return count;
    }

    @Test
    void whenMoveAll_thenNoAnimalsLostOrDuplicated() {
        Set<Animal> animalSet = new HashSet<>();
        for (AnimalType type : AnimalType.values()) {
            for (int i = 0; i < 10; i++) {
                Animal animal1 = factory.create(type);
                cell.addAnimal(animal1);
                animalSet.add(animal1);
            }
        }

        movementService.moveAll(randomService);
        Set<Animal> animalsAfterMove = new HashSet<>();
        for (Cell[] row : island.getCells()) {
            for (Cell column : row) {
                for (Animal animal1 : column.getAnimalsList()) {
                    animalsAfterMove.add(animal1);
                }
            }
        }

        int count = 0;
        for (Cell[] row : island.getCells()) {
            for (Cell column : row) {
                count += column.getAnimalsList().size();
            }
        }

        assertEquals(animalSet, animalsAfterMove);
        assertEquals(animalsAfterMove.size(), count);

    }

    @Test
    void whenAnimalIndeedMovesFromOneCellToAnother() {
        Cell cell1 = island.getCells()[2][2];
        cell1.addAnimal(animal);
        Cell cell2 = island.getCells()[2][4];
        movement = new Movement(animal, cell1, cell2);
        doReturn(movement).when(movementService).createMovement(animal, 2, 2, randomService);
        movementService.moveAll(randomService);
        assertTrue(cell1.getAnimalsList().isEmpty());
        assertTrue(cell2.getAnimalsList().contains(animal));

    }

    @Test
    void whenMoveAllThrows(){
        Cell fallingCell = mock(Cell.class);

        when(fallingCell.getAnimalsList()).thenThrow(new RuntimeException("Test exception"));

        island.getCells()[0][0] = fallingCell;

        assertThrows(RuntimeException.class, () -> movementService.moveAll(randomService));
    }

    @Test
    void whenOneAnimalMoves_itExistsInExactlyOneCell(){
        Animal wolf = factory.create(WOLF);
        cell.addAnimal(wolf);
        movementService.moveAll(randomService);

        int count = 0;
        for (Cell[] row: island.getCells()){
            for (Cell currentCell: row){
                if (currentCell.getAnimalsList().contains(wolf)){
                    count++;
                }
            }
        }



        assertEquals(1, count);
    }

    @Test
    void whenOneAnimalMakesOneMovement(){
        Animal wolf = factory.create(WOLF);
        cell.addAnimal(wolf);
        Movement movement1 = new Movement(animal, cell, island.getCells()[2][3]);

        doReturn(movement1).when(movementService).createMovement(eq(wolf), anyInt(), anyInt(), eq(randomService));

        movementService.moveAll(randomService);

        verify(movementService, times(1)).createMovement(eq(wolf), anyInt(), anyInt(), eq(randomService));

    }
    @Test
    void whenReproduceWorksAfterMovement(){
        when(randomService.getRandomNumber(animal.getConfig().getSpeed())).thenReturn(1);
        when(randomService.getRandomDirection(3)).thenReturn(1);

        Animal wolfPapa = factory.create(AnimalType.WOLF);
        Animal wolfMama = factory.create(AnimalType.WOLF);

        cell.addAnimal(wolfPapa);
        cell.addAnimal(wolfMama);

        long wolfBefore = cell.getCurrentAnimalCount(AnimalType.WOLF);

        movementService.moveAll(randomService);


        for (int i = 0; i < island.getCells().length; i++) {
            for (int j = 0; j < island.getCells()[i].length; j++) {
                Cell currentCell = island.getCells()[i][j];

                if (currentCell.getAnimalsList().contains(wolfPapa)){
                    System.out.println("Wolf located here: [" + i + "] ["  + j + "]");
                }
            }
        }
    }

}
