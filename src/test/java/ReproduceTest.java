import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.javarush.iliasov.islandproject.Cell;
import ru.javarush.iliasov.islandproject.Island;
import ru.javarush.iliasov.islandproject.Organisms.Animal;
import ru.javarush.iliasov.islandproject.Organisms.AnimalFactory;

import ru.javarush.iliasov.islandproject.Organisms.ConfigLoader;
import ru.javarush.iliasov.islandproject.Services.RandomService;
import ru.javarush.iliasov.islandproject.Services.ReproduceService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static ru.javarush.iliasov.islandproject.Organisms.AnimalType.*;

class ReproduceTest {
    AnimalFactory factory;
    Cell cell;
    ReproduceService reproduceService;
    Animal animal;
    Island island;
    List<Animal> pair;
    RandomService randomService;

    @BeforeEach
    void init() throws IOException {
        island = new Island(new Cell[5][5]);
        factory  = new AnimalFactory(new ConfigLoader());
        randomService = Mockito.mock(RandomService.class);
        reproduceService = new ReproduceService(factory, island, randomService);
        pair = new ArrayList<>();
        for (int i = 0; i < island.getCells().length; i++) {
            for (int j = 0; j < island.getCells()[i].length; j++) {
                island.getCells()[i][j] = new Cell(new ArrayList<>(), new ArrayList<>());
            }
        }

        cell = island.getCells()[2][2];
    }

    @AfterEach
    void ini1() {
        reproduceService.shutdown();
    }

    @Test
    void whenHasSpaceInCell(){
        cell.addAnimal(animal);
        assertTrue(reproduceService.hasSpaceInCell(animal, cell));
    }

    @Test
    void whenNoSpaceInCell(){
        for (int i = 0; i < animal.getConfig().getMaxInCell(); i++) {
            cell.addAnimal(factory.create(WOLF));
        }
        assertFalse(reproduceService.hasSpaceInCell(animal, cell));
    }

    @Test
    void whenAtLeastTwoAnimals(){
        cell.addAnimal(animal);
        assertFalse( reproduceService.hasAtLeastTwoAnimals(animal, cell));
    }

    @Test
    void whenPairFoundReturnsTwoAnimals(){
        for (int i = 0; i < animal.getConfig().getMaxInCell()-1; i++) {
            cell.addAnimal(animal);
        }
        assertEquals(2, reproduceService.findPair(animal, cell).size());
    }

    @Test
    void whenReproduceReturnsNewAnimal() {
        long maxCount = animal.getConfig().getMaxInCell();
        for (int i = 0; i < maxCount - 1; i++) {
                cell.addAnimal(factory.create(animal.getType()));
        }
        long animalCount = cell.getCurrentAnimalCount(animal.getType());
        reproduceService.reproduce(animal, cell);
        long animalCountAfterReproduce = cell.getCurrentAnimalCount(animal.getType());
        assertEquals(animalCount + 1, animalCountAfterReproduce);
    }

    @Test
    void whenMaxInCellIsFull(){
        for (int i = 0; i < animal.getConfig().getMaxInCell(); i++) {
            cell.addAnimal(factory.create(animal.getType()));
        }
        long animalCount = cell.getCurrentAnimalCount(animal.getType());
        reproduceService.reproduce(animal, cell);
        long count = cell.getCurrentAnimalCount(animal.getType());
        assertEquals(animalCount , count);
    }

    @Test
    void whenOnlyOneAnimalInCell_thenNoReproduction() {
        cell.addAnimal(factory.create(animal.getType()));
        long animalCount = cell.getCurrentAnimalCount(animal.getType());
        reproduceService.reproduceCell(cell);
        long animalCountAfterReproduce = cell.getCurrentAnimalCount(animal.getType());
        assertEquals(animalCount, animalCountAfterReproduce);
    }


    @Test
    void whenPercentIs49AnimalShouldNotReproduce() {
        RandomService randomService = mock(RandomService.class);
        when(randomService.randomNumber()).thenReturn(49);
        for (int i = 0; i < animal.getConfig().getMaxInCell() -1; i++) {
            cell.addAnimal(factory.create(animal.getType()));
        }
        long currentAnimalNumber = cell.getCurrentAnimalCount(animal.getType());
        reproduceService.reproducePercent();
        long numberAfterReproduce = cell.getCurrentAnimalCount(animal.getType());
        assertEquals(currentAnimalNumber, numberAfterReproduce);
    }

    @Test
    void whenPercentIs50AnimalShouldReproduce() {
        RandomService randomService = mock(RandomService.class);
        when(randomService.randomNumber()).thenReturn(50);
        for (int i = 0; i < animal.getConfig().getMaxInCell() - 1; i++) {
            cell.addAnimal(factory.create(animal.getType()));
        }
        long currentAnimalNumber = cell.getCurrentAnimalCount(animal.getType());
        reproduceService.reproducePercent();
        long numberAfterReproduce = cell.getCurrentAnimalCount(animal.getType());
        assertEquals(currentAnimalNumber + 1, numberAfterReproduce);
    }

    @Test
    void whenTwoAnimalsInCell_thenReproduceAllCreatesChild(){
        cell.addAnimal(factory.create(animal.getType()));
        cell.addAnimal(factory.create(animal.getType()));
        long currentAnimalNumber = cell.getCurrentAnimalCount(animal.getType());
        reproduceService.reproduceAll();
        long numberAfterReproduceAll = cell.getCurrentAnimalCount(animal.getType());
        assertEquals(currentAnimalNumber + 1, numberAfterReproduceAll);
    }

    @Test
    void whenWorkersReproduceService(){
        for (int i = 0; i < animal.getConfig().getMaxInCell() - 1; i++) {
            cell.addAnimal(factory.create(animal.getType()));
        }
        long animalCount = cell.getCurrentAnimalCount(animal.getType());
        reproduceService.reproduceAll();
        long animalCountAfterReproduce = cell.getCurrentAnimalCount(animal.getType());
        assertEquals(animalCount + 1, animalCountAfterReproduce);
    }

    @Test
    void whenMultipleCellsHavePairs_thenAllCellsAreReproduced(){
        when(randomService.randomNumber()).thenReturn(34);
        Cell wolfCell = new Cell(new ArrayList<>(), new ArrayList<>());
        Cell rabbitCell = new Cell(new ArrayList<>(), new ArrayList<>());
        Cell buffaloCell = new Cell(new ArrayList<>(), new ArrayList<>());

        wolfCell.addAnimal(factory.create(WOLF));
        wolfCell.addAnimal(factory.create(WOLF));

        rabbitCell.addAnimal(factory.create(RABBIT));
        rabbitCell.addAnimal(factory.create(RABBIT));

        buffaloCell.addAnimal(factory.create(BUFFALO));
        buffaloCell.addAnimal(factory.create(BUFFALO));

        island.getCells()[1][2] = wolfCell;
        island.getCells()[2][3] = rabbitCell;
        island.getCells()[4][2] = buffaloCell;

        long wolfCount = wolfCell.getCurrentAnimalCount(WOLF);
        long rabbitCount = rabbitCell.getCurrentAnimalCount(RABBIT);
        long buffaloCount = buffaloCell.getCurrentAnimalCount(BUFFALO);

        reproduceService.reproduceAll();

        long wolfCountAfterReproduce = wolfCell.getCurrentAnimalCount(WOLF);
        long rabbitCountAfterReproduce = rabbitCell.getCurrentAnimalCount(RABBIT);
        long buffaloCountAfterReproduce = buffaloCell.getCurrentAnimalCount(BUFFALO);


        assertEquals(wolfCount + 1, wolfCountAfterReproduce);
        assertEquals(rabbitCount + 1, rabbitCountAfterReproduce);
        assertEquals(buffaloCount + 1, buffaloCountAfterReproduce);
    }

    @Test
    void whenShutdown_thenWorkersAreShutdown(){
        reproduceService.reproduceAll();
        reproduceService.shutdown();
        assertTrue(reproduceService.isShutdown()) ;
    }

    @Test
    void whenMultiThreaded(){
        when(randomService.randomNumber()).thenReturn(35);
        Cell wolfCell = new Cell(new ArrayList<>(), new ArrayList<>());
        Cell rabbitCell = new Cell(new ArrayList<>(), new ArrayList<>());
        Cell buffaloCell = new Cell(new ArrayList<>(), new ArrayList<>());
        Cell deerCell = new Cell(new ArrayList<>(), new ArrayList<>());
        Cell duckCell = new Cell(new ArrayList<>(), new ArrayList<>());
        island.getCells()[1][2] = wolfCell;
        island.getCells()[3][2] = rabbitCell;
        island.getCells()[2][2] = buffaloCell;
        island.getCells()[2][4] = deerCell;
        island.getCells()[4][2] = duckCell;

        for (int i = 0; i < factory.create(WOLF).getConfig().getMaxInCell() - 10; i++) {
            wolfCell.addAnimal(factory.create(WOLF));
        }
        for (int i = 0; i < factory.create(RABBIT).getConfig().getMaxInCell() - 10; i++) {
            rabbitCell.addAnimal(factory.create(RABBIT));
        }
        for (int i = 0; i < factory.create(BUFFALO).getConfig().getMaxInCell() - 1; i++) {
            buffaloCell.addAnimal(factory.create(BUFFALO));
        }
        for (int i = 0; i < factory.create(DEER).getConfig().getMaxInCell() - 10; i++) {
            deerCell.addAnimal(factory.create(DEER));
        }
        for (int i = 0; i < factory.create(DUCK).getConfig().getMaxInCell() - 10; i++) {
            duckCell.addAnimal(factory.create(DUCK));
        }

        long wolfCount = wolfCell.getCurrentAnimalCount(WOLF);
        long rabbitCount = rabbitCell.getCurrentAnimalCount(RABBIT);
        long buffaloCount = buffaloCell.getCurrentAnimalCount(BUFFALO);
        long deerCount = deerCell.getCurrentAnimalCount(DEER);
        long duckCount = duckCell.getCurrentAnimalCount(DUCK);

        reproduceService.reproduceAll();

        long wolfCountAfterReproduce = wolfCell.getCurrentAnimalCount(WOLF);
        long rabbitCountAfterReproduce = rabbitCell.getCurrentAnimalCount(RABBIT);
        long buffaloCountAfterReproduce = buffaloCell.getCurrentAnimalCount(BUFFALO);
        long deerCountAfterReproduce = deerCell.getCurrentAnimalCount(DEER);
        long duckCountAfterReproduce = duckCell.getCurrentAnimalCount(DUCK);

        assertEquals(wolfCount + 1, wolfCountAfterReproduce);
        assertEquals(rabbitCount + 1, rabbitCountAfterReproduce);
        assertEquals(buffaloCount + 1, buffaloCountAfterReproduce);
        assertEquals(deerCount + 1, deerCountAfterReproduce);
        assertEquals(duckCount + 1, duckCountAfterReproduce);
    }

    @Test
    void whenReproduceAllThrows(){
        Cell failingCell = mock(Cell.class);

        when(failingCell.getAnimalsList()).thenThrow(new RuntimeException("Test exception"));

        island.getCells()[0][0] = failingCell;

        assertThrows(RuntimeException.class, () -> reproduceService.reproduceAll());
    }
}
