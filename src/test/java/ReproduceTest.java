import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.javarush.iliasov.cryptoanalyzer.Cell;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.Animal;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.AnimalFactory;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.AnimalType;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.ConfigLoader;
import ru.javarush.iliasov.cryptoanalyzer.Services.RandomService;
import ru.javarush.iliasov.cryptoanalyzer.Services.ReproduceService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.javarush.iliasov.cryptoanalyzer.Organisms.AnimalType.WOLF;

public class ReproduceTest {
    AnimalFactory factory;
    Cell cell;
    ReproduceService reproduceService;
    Animal animal;

    @BeforeEach
    void init() throws IOException {
        factory  = new AnimalFactory(new ConfigLoader());
        cell = new Cell(new ArrayList<>(), new ArrayList<>());
        reproduceService = new ReproduceService(factory);
        animal = factory.create(WOLF);
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
    void whenReproduceReturnsNewAnimal() throws IOException {
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
    void whenMaxInCellIsFull() throws IOException {
        for (int i = 0; i < animal.getConfig().getMaxInCell(); i++) {
            cell.addAnimal(factory.create(animal.getType()));
        }
        long animalCount = cell.getCurrentAnimalCount(animal.getType());
        reproduceService.reproduce(animal, cell);
        long count = cell.getCurrentAnimalCount(animal.getType());
        assertEquals(animalCount , count);
    }

    @Test
    void whenNoPairFound() throws IOException {
        cell.addAnimal(factory.create(animal.getType()));
        long animalCount = cell.getCurrentAnimalCount(animal.getType());
        reproduceService.reproduce(animal,cell);
        long animalCountAfterReproduce = cell.getCurrentAnimalCount(animal.getType());
        assertEquals(animalCount, animalCountAfterReproduce);
    }


    @Test
    void whenPercentIs49AnimalShouldNotReproduce() throws IOException {
        RandomService randomService = Mockito.mock(RandomService.class);
        Mockito.when(randomService.randomNumber()).thenReturn(49);
        for (int i = 0; i < animal.getConfig().getMaxInCell() -1; i++) {
            cell.addAnimal(factory.create(animal.getType()));
        }
        long currentAnimalNumber = cell.getCurrentAnimalCount(animal.getType());
        reproduceService.reproducePercent(animal, cell, randomService);
        long numberAfterReproduce = cell.getCurrentAnimalCount(animal.getType());
        assertEquals(currentAnimalNumber, numberAfterReproduce);
    }

    @Test
    void whenPercentIs50AnimalShouldReproduce() throws IOException {
        RandomService randomService = Mockito.mock(RandomService.class);
        Mockito.when(randomService.randomNumber()).thenReturn(50);
        for (int i = 0; i < animal.getConfig().getMaxInCell() - 1; i++) {
            cell.addAnimal(factory.create(animal.getType()));
        }
        long currentAnimalNumber = cell.getCurrentAnimalCount(animal.getType());
        reproduceService.reproducePercent(animal, cell, randomService);
        long numberAfterReproduce = cell.getCurrentAnimalCount(animal.getType());
        assertEquals(currentAnimalNumber + 1, numberAfterReproduce);
    }
}
