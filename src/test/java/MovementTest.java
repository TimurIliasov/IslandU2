import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    private RandomService randomService;
    Movement movement;
    MovementService movementService;

    @BeforeEach
    void init() throws IOException {
        factory = new AnimalFactory(new ConfigLoader());
        island = new Island(new Cell[5][5]);
        randomService = new RandomService();
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
    void testingMovementServiceReallyWorks() throws IOException {

        for (int i = 0; i < island.getCells().length; i++) {
            for (int j = 0; j < island.getCells()[i].length; j++) {
                Cell cell = new Cell(new ArrayList<>(), new ArrayList<>());
                island.getCells()[i][j] = cell;
            }
        }


        ConfigLoader configLoader = new ConfigLoader();
        AnimalFactory factory = new AnimalFactory(configLoader);
        Animal animal = factory.create(WOLF);
        island.getCells()[2][2].addAnimal(animal);
        System.out.println(animal.getConfig().getSpeed());

        MovementService movementService = new MovementService(island);
        movementService.moveAll(randomService);

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
        Set<Animal> animalSet1 = new HashSet<>();
        for (Cell[] row : island.getCells()) {
            for (Cell column : row) {
                for (Animal animal1 : column.getAnimalsList()) {
                    animalSet1.add(animal1);
                }
            }
        }

        int count = 0;
        for (Cell[] row : island.getCells()) {
            for (Cell column : row) {
                count += column.getAnimalsList().size();
            }
        }

        assertEquals(animalSet, animalSet1);
        assertEquals(animalSet1.size(), count);

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
}
