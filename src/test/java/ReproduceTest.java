import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.javarush.iliasov.islandproject.Cell;
import ru.javarush.iliasov.islandproject.Island;
import ru.javarush.iliasov.islandproject.Organisms.*;

import ru.javarush.iliasov.islandproject.Services.EatingService;
import ru.javarush.iliasov.islandproject.Services.RandomService;
import ru.javarush.iliasov.islandproject.Services.ReproduceService;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;


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
    Cell wolfReproduce;
    Cell rabbitReproduce;
    Cell foxReproduce;
    Cell duckReproduce;

    @BeforeEach
    void init() throws IOException {
        island = new Island(new Cell[5][5]);
        factory = new AnimalFactory(new ConfigLoader());
        randomService = Mockito.mock(RandomService.class);
        reproduceService = new ReproduceService(factory, island, randomService);
        animal = factory.create(WOLF);
        pair = new ArrayList<>();
        for (int i = 0; i < island.getCells().length; i++) {
            for (int j = 0; j < island.getCells()[i].length; j++) {
                island.getCells()[i][j] = new Cell(new ArrayList<>(), new ArrayList<>());
            }
        }

        cell = island.getCells()[2][2];
        wolfReproduce = island.getCells()[1][2];
        rabbitReproduce = island.getCells()[3][3];
        foxReproduce = island.getCells()[4][3];
        duckReproduce = island.getCells()[2][3];
    }

    @AfterEach
    void ini1() {
        reproduceService.shutdown();
    }

    @Test
    void whenHasSpaceInCell() {
        cell.addAnimal(animal);
        assertTrue(reproduceService.hasSpaceInCell(animal, cell));
    }

    @Test
    void whenNoSpaceInCell() {
        for (int i = 0; i < animal.getConfig().getMaxInCell(); i++) {
            cell.addAnimal(factory.create(WOLF));
        }
        assertFalse(reproduceService.hasSpaceInCell(animal, cell));
    }

    @Test
    void whenAtLeastTwoAnimals() {
        cell.addAnimal(animal);
        assertFalse(reproduceService.hasAtLeastTwoAnimals(animal, cell));
    }

    @Test
    void whenPairFoundReturnsTwoAnimals() {
        for (int i = 0; i < animal.getConfig().getMaxInCell() - 1; i++) {
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
    void whenMaxInCellIsFull() {
        for (int i = 0; i < animal.getConfig().getMaxInCell(); i++) {
            cell.addAnimal(factory.create(animal.getType()));
        }
        long animalCount = cell.getCurrentAnimalCount(animal.getType());
        reproduceService.reproduceAll();
        long count = cell.getCurrentAnimalCount(animal.getType());
        assertEquals(animalCount, count);
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
        for (int i = 0; i < animal.getConfig().getMaxInCell() - 1; i++) {
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
        reproduceService.reproduceAll();
        long numberAfterReproduce = cell.getCurrentAnimalCount(animal.getType());
        assertEquals(currentAnimalNumber + 1, numberAfterReproduce);
    }

    @Test
    void whenTwoAnimalsInCell_thenReproduceAllCreatesChild() {
        cell.addAnimal(factory.create(animal.getType()));
        cell.addAnimal(factory.create(animal.getType()));
        long currentAnimalNumber = cell.getCurrentAnimalCount(animal.getType());
        reproduceService.reproduceAll();
        long numberAfterReproduceAll = cell.getCurrentAnimalCount(animal.getType());
        assertEquals(currentAnimalNumber + 1, numberAfterReproduceAll);
    }

    @Test
    void whenWorkersReproduceService() {
        for (int i = 0; i < animal.getConfig().getMaxInCell() - 1; i++) {
            cell.addAnimal(factory.create(animal.getType()));
        }
        long animalCount = cell.getCurrentAnimalCount(animal.getType());
        System.out.println(animalCount);
        reproduceService.reproduceAll();
        long animalCountAfterReproduce = cell.getCurrentAnimalCount(animal.getType());
        System.out.println(animalCountAfterReproduce);
        assertEquals(animalCount + 1, animalCountAfterReproduce);
    }

    @Test
    void whenMultipleCellsHavePairs_thenAllCellsAreReproduced() {
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
    void whenShutdown_thenWorkersAreShutdown() {
        reproduceService.reproduceAll();
        reproduceService.shutdown();
        assertTrue(reproduceService.isShutdown());
    }

    @Test
    void whenReproduceAllThrows() {
        Cell failingCell = mock(Cell.class);

        when(failingCell.getAnimalsList()).thenThrow(new RuntimeException("Test exception"));

        island.getCells()[0][0] = failingCell;

        assertThrows(RuntimeException.class, () -> reproduceService.reproduceAll());
    }

    @Test
    void whenReproduceAllStressTest() throws InterruptedException, ExecutionException, TimeoutException {
        int threads = 10;
        int tasks = 1000;

        for (int i = 0; i < 10; i++) {
            wolfReproduce.addAnimal(factory.create(WOLF));
            rabbitReproduce.addAnimal(factory.create(RABBIT));
            foxReproduce.addAnimal(factory.create(FOX));
            duckReproduce.addAnimal(factory.create(DUCK));
        }


        List<Future> futures = new ArrayList<>();
        ExecutorService worker = Executors.newFixedThreadPool(threads);
        CountDownLatch startGate = new CountDownLatch(1);

        try {
            for (int i = 0; i < tasks; i++) {
                futures.add(worker.submit(() -> {
                    startGate.await();
                    reproduceService.reproduceAll();
                    return null;
                }));
            }
            startGate.countDown();
            for (Future<?> future : futures) {
                future.get(10, TimeUnit.SECONDS);
            }
        } finally {
            worker.shutdown();
            if (!worker.awaitTermination(5, TimeUnit.SECONDS)) {
                worker.shutdownNow();
            }
        }

        Map<AnimalType, Integer> animalMap = new EnumMap(AnimalType.class);
        for (AnimalType type : AnimalType.values()) {
            animalMap.put(type, factory.create(type).getConfig().getMaxInCell());
        }

        for (Cell[] row : island.getCells()) {
            for (Cell currentCell : row) {
                for (AnimalType type : AnimalType.values()) {
                    long currentCount = currentCell.getCurrentAnimalCount(type);
                    long actuaclCount = currentCell.getAnimalsList().stream().
                            filter(animal1 -> animal1.getType() == type)
                            .count();
                    assertEquals(actuaclCount, currentCount);
                    assertTrue(currentCount <= animalMap.get(type),
                            () -> type + " exceeded max in cell " + currentCount);
                }
            }
        }

    }

    @Test
    void whenMaxInCellIsAlmostFull() throws ExecutionException, InterruptedException {
        Animal wolf = factory.create(WOLF);
        for (int i = 0; i < wolf.getConfig().getMaxInCell() - 1; i++) {
            cell.addAnimal(factory.create(WOLF));
        }

        ExecutorService worker = Executors.newFixedThreadPool(10);
        List<Future> futures = new ArrayList<>();
        try {
            for (int i = 0; i < 1000; i++) {
                futures.add(worker.submit(() -> reproduceService.reproduceAll()));
            }


            for (Future<?> future : futures) {
                future.get();
            }
        } finally {
            worker.shutdown();

            if (!worker.awaitTermination(5, TimeUnit.SECONDS)) {
                worker.shutdownNow();
            }
        }

        long animalsAfter = cell.getCurrentAnimalCount(WOLF);
        long actualCount = cell.getAnimalsList().stream()
                .filter(animal1 -> animal1.getType() == WOLF)
                .count();

        assertEquals(actualCount, animalsAfter);
        assertEquals(wolf.getConfig().getMaxInCell(), animalsAfter);
    }


    @Test
    void whenReproduceAfterEating(){
        EatingService eatingService = new EatingService(island);
        when(randomService.randomNumber()).thenReturn(24);

        for (int i = 0; i < 25; i++) {
            cell.addAnimal(factory.create(WOLF));
        }

        long currentWolf = cell.getCurrentAnimalCount(WOLF);
        System.out.println("WOLFS befor: " + currentWolf);

        for (int i = 0; i < 130; i++) {
            cell.addAnimal(factory.create(RABBIT));
            cell.addPlant(new Plants());
        }

        long currentRabbit = cell.getCurrentAnimalCount(RABBIT);
        System.out.println("RABBITS befor: " + currentRabbit);

        eatingService.eatAllCells(randomService);

        long afterRabbit = cell.getCurrentAnimalCount(RABBIT);
        System.out.println("RABBITS after: " + afterRabbit);

        long afterWolf = cell.getCurrentAnimalCount(WOLF);
        System.out.println("WOLVES after: " + afterWolf);

        reproduceService.reproduceAll();

        long rabbitsReproduced = cell.getCurrentAnimalCount(RABBIT);
        long wolvesReproduced = cell.getCurrentAnimalCount(WOLF);

        System.out.println("RABBITS after reproduce: " + rabbitsReproduced);
        System.out.println("WOLVES after reproduce: " + wolvesReproduced);

        assertEquals(afterRabbit + 1, rabbitsReproduced);
        assertEquals(afterWolf + 1, wolvesReproduced);
    }
}
