import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.javarush.iliasov.islandproject.Cell;
import ru.javarush.iliasov.islandproject.Island;
import ru.javarush.iliasov.islandproject.Organisms.*;
import ru.javarush.iliasov.islandproject.Services.EatingService;
import ru.javarush.iliasov.islandproject.Services.RandomService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EatTest {
    ConfigLoader configLoader;
    AnimalFactory factory;
    Cell cell;
    Animal hunter;
    Animal prey;
    EatingService eatingService;
    Island island;
    RandomService randomService;
    @BeforeEach
    void init() throws IOException {
        island = new Island(new Cell[5][5]);
        configLoader = new ConfigLoader();
        factory = new AnimalFactory(configLoader);
        hunter = factory.create(AnimalType.WOLF);
        prey = factory.create(AnimalType.RABBIT);
        eatingService = spy(new EatingService(island));
        randomService = mock(RandomService.class);
        for (int i = 0; i < island.getCells().length; i++) {
            for (int j = 0; j < island.getCells()[i].length; j++) {
                Cell currentCell = new Cell(new ArrayList<>(), new ArrayList<>());
                island.getCells()[i][j] = currentCell;
            }
        }

        cell = island.getCells()[2][2];
    }

    @AfterEach
    void tearDown(){
        eatingService.shutdown();
    }

    @Test
    void whenAnimalEatsAnimalGainsWeight()  {
        when(randomService.randomNumber()).thenReturn(24);
        cell.addAnimal(hunter);
        cell.addAnimal(prey);

        double hunterWeight = hunter.getWeight();
        double preyWeight = prey.getWeight();

        eatingService.eatAllCells(randomService);
        assertFalse(prey.getAlive());
        assertEquals(hunterWeight + preyWeight, hunter.getWeight(), 0.0001);
    }

    @Test
    void whenHunterCouldNotEatPrey(){
        cell.addAnimal(hunter);
        cell.addAnimal(prey);
        when(randomService.randomNumber()).thenReturn(78);

        double hunterWeight = hunter.getWeight();

        eatingService.eatAllCells(randomService);
        assertTrue(prey.getAlive());
        assertEquals(hunterWeight, hunter.getWeight());
    }

    @Test
    void whenAnimalEatsPlant(){
        Plants plants = new Plants();
        cell.addPlant(plants);
        cell.addAnimal(prey);

        double plantWeight = plants.getWeight();
        double preyWeight = prey.getWeight();

        eatingService.eatPlant(prey, plants, cell);
        assertEquals(preyWeight + plantWeight, prey.getWeight());
        assertFalse(cell.getPlantsList().contains(plants));
    }

    @Test
    void whenAnimalGetsEaten_thenItsRemovedFromCell(){
        when(randomService.randomNumber()).thenReturn(24);

        cell.addAnimal(hunter);
        cell.addAnimal(prey);

        eatingService.eatAllCells(randomService);

        assertFalse(prey.getAlive());
        assertFalse(cell.getAnimalsList().contains(prey));
        assertTrue(cell.getAnimalsList().contains(hunter));
    }

    @Test
    void whenEatAll_thenNoAnimalsLostOrDuplicated(){
        Set<Animal> animalSet = new HashSet<>();
        when(randomService.randomNumber()).thenReturn(24);

        for (int i = 0; i < 3; i++) {
            Animal newHunter = factory.create(hunter.getType());
            Animal newPrey = factory.create(prey.getType());

            cell.addAnimal(newHunter);
            cell.addAnimal(newPrey);

            animalSet.add(newHunter);
            animalSet.add(newPrey);
        }

        long hunterCount = cell.getCurrentAnimalCount(hunter.getType());
        long preyCount = cell.getCurrentAnimalCount(prey.getType());


        eatingService.eatAllCells(randomService);

        Set<Animal> animalSet1 = new HashSet<>();
        for (Cell[] row: island.getCells()){
            for (Cell column: row){
                for (Animal animal: column.getAnimalsList()){
                    animalSet1.add(animal);
                }
            }
        }

        Set<Animal> expectedAnimals = new HashSet<>(animalSet);
        expectedAnimals.removeIf(animal -> !animal.getAlive());

        long hunterCountAfterEating = cell.getCurrentAnimalCount(hunter.getType());
        long preyCountAfterEating = cell.getCurrentAnimalCount(prey.getType());


        assertEquals(preyCount - 3, preyCountAfterEating);
        assertEquals(hunterCount, hunterCountAfterEating);

        assertEquals(expectedAnimals, animalSet1);

    }

    @Test
    void whenAnimalEatsPlant_thenAnimalGainsWeight(){
        Animal animal = factory.create(prey.getType());
        cell.addAnimal(animal);

        Plants plants = new Plants();
        cell.addPlant(plants);

        double animalWeight = animal.getWeight();
        double plantWeight = plants.getWeight();

        eatingService.eatAllCells(randomService);

        double animalWeightAfterEating = animal.getWeight();

        assertFalse(cell.getPlantsList().contains(plants));
        assertEquals(animalWeight + plantWeight, animalWeightAfterEating, 0.0001);
    }

    @Test
    void whenCellsEmpty_thenEatAllDoesNotThrow(){
        assertDoesNotThrow(() -> eatingService.eatAllCells(randomService));
    }

    @Test
    void whenInCellsOnlyAnimals_thenEatAllDoesNotThrow(){
        Animal animal = factory.create(hunter.getType());
        cell.addAnimal(animal);

        assertDoesNotThrow(() -> eatingService.eatAllCells(randomService));
    }


    @Test
    void whenEatAllCells_thenAllCellsAreProcessed(){
        Cell wolfRabbit = new Cell(new ArrayList<>(), new ArrayList<>());
        Cell wolfRabbit1 = new Cell(new ArrayList<>(), new ArrayList<>());
        Cell wolfRabbit2 = new Cell(new ArrayList<>(), new ArrayList<>());
        Cell wolfRabbit3 = new Cell(new ArrayList<>(), new ArrayList<>());

        island.getCells()[1][2] = wolfRabbit;
        island.getCells()[2][3] = wolfRabbit1;
        island.getCells()[1][3] = wolfRabbit2;
        island.getCells()[3][2] = wolfRabbit3;

        for (int i = 0; i < hunter.getConfig().getMaxInCell(); i++) {
            wolfRabbit.addAnimal(factory.create(AnimalType.WOLF));
            wolfRabbit.addAnimal(factory.create(AnimalType.RABBIT));

            wolfRabbit1.addAnimal(factory.create(AnimalType.WOLF));
            wolfRabbit1.addAnimal(factory.create(AnimalType.RABBIT));

            wolfRabbit2.addAnimal(factory.create(AnimalType.WOLF));
            wolfRabbit2.addAnimal(factory.create(AnimalType.RABBIT));

            wolfRabbit3.addAnimal(factory.create(AnimalType.WOLF));
            wolfRabbit3.addAnimal(factory.create(AnimalType.RABBIT));
        }

        long rabbitCount = wolfRabbit.getCurrentAnimalCount(AnimalType.RABBIT);
        long rabbitCount1 = wolfRabbit1.getCurrentAnimalCount(AnimalType.RABBIT);
        long rabbitCount2 = wolfRabbit2.getCurrentAnimalCount(AnimalType.RABBIT);
        long rabbitCount3 = wolfRabbit3.getCurrentAnimalCount(AnimalType.RABBIT);
        long wolfCount = wolfRabbit.getCurrentAnimalCount(AnimalType.WOLF);

        eatingService.eatAllCells(randomService);

        long rabbitCountAfter = wolfRabbit.getCurrentAnimalCount(AnimalType.RABBIT);
        long rabbitCountAfter1 = wolfRabbit1.getCurrentAnimalCount(AnimalType.RABBIT);
        long rabbitCountAfter2 = wolfRabbit2.getCurrentAnimalCount(AnimalType.RABBIT);
        long rabbitCountAfter3 = wolfRabbit3.getCurrentAnimalCount(AnimalType.RABBIT);
        long wolfCountAfter = wolfRabbit.getCurrentAnimalCount(AnimalType.WOLF);
        assertEquals(rabbitCount - 30, rabbitCountAfter);
        assertEquals(rabbitCount1 - 30, rabbitCountAfter1);
        assertEquals(rabbitCount2 - 30, rabbitCountAfter2);
        assertEquals(rabbitCount3 - 30, rabbitCountAfter3);
        assertEquals(wolfCount, wolfCountAfter);
    }

    @Test
    void whenEatAllThrows(){
        Cell fallingCell = mock(Cell.class);
        when(fallingCell.getAnimalsList()).thenThrow(new RuntimeException("Test exception"));

        island.getCells()[0][0] = fallingCell;

        assertThrows(RuntimeException.class, () -> eatingService.eatAllCells(randomService));
    }
}

