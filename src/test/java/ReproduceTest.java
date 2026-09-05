import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import static ru.javarush.iliasov.islandproject.Organisms.AnimalType.WOLF;

class ReproduceTest {
    AnimalFactory factory;
    Cell cell;
    ReproduceService reproduceService;
    Animal animal;
    Island island;
    List<Animal> pair;

    @BeforeEach
    void init() throws IOException {
        island = new Island(new Cell[5][5]);
        factory  = new AnimalFactory(new ConfigLoader());
        reproduceService = new ReproduceService(factory, island);
        animal = factory.create(WOLF);
        pair = new ArrayList<>();
        for (int i = 0; i < island.getCells().length; i++) {
            for (int j = 0; j < island.getCells()[i].length; j++) {
                island.getCells()[i][j] = new Cell(new ArrayList<>(), new ArrayList<>());
            }
        }

        cell = island.getCells()[2][2];
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
        reproduceService.reproduce(animal, cell, pair);
        long animalCountAfterReproduce = cell.getCurrentAnimalCount(animal.getType());
        assertEquals(animalCount + 1, animalCountAfterReproduce);
    }

    @Test
    void whenMaxInCellIsFull(){
        for (int i = 0; i < animal.getConfig().getMaxInCell(); i++) {
            cell.addAnimal(factory.create(animal.getType()));
        }
        long animalCount = cell.getCurrentAnimalCount(animal.getType());
        reproduceService.reproduce(animal, cell, pair);
        long count = cell.getCurrentAnimalCount(animal.getType());
        assertEquals(animalCount , count);
    }

    @Test
    void whenNoPairFound() {
        cell.addAnimal(factory.create(animal.getType()));
        long animalCount = cell.getCurrentAnimalCount(animal.getType());
        reproduceService.reproduce(animal,cell, pair);
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
        reproduceService.reproducePercent(animal, cell, randomService);
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
        reproduceService.reproducePercent(animal, cell, randomService);
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
}
