import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Spy;
import ru.javarush.iliasov.islandproject.Cell;
import ru.javarush.iliasov.islandproject.Exceptions.MaxInCellException;
import ru.javarush.iliasov.islandproject.Island;
import ru.javarush.iliasov.islandproject.Organisms.Animal;
import ru.javarush.iliasov.islandproject.Organisms.AnimalFactory;
import ru.javarush.iliasov.islandproject.Organisms.AnimalType;
import ru.javarush.iliasov.islandproject.Organisms.ConfigLoader;
import ru.javarush.iliasov.islandproject.Services.EatingService;
import ru.javarush.iliasov.islandproject.Services.RandomService;
import ru.javarush.iliasov.islandproject.Services.ReproduceService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Parametrized {
    Island island;
    Cell cell;
    AnimalFactory factory;
    @Spy
    EatingService eatingService;
    @Spy
    ReproduceService reproduceService;
    RandomService randomService;

    @BeforeAll
    static void setup(){
        System.out.println("Test executed: ");
    }

    @BeforeEach
    void init(){
        island = new Island(new Cell[5][5]);
        factory = new AnimalFactory(new ConfigLoader());
        randomService = mock(RandomService.class);
        eatingService = new EatingService(island);
        reproduceService = new ReproduceService(factory, island, randomService);

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
        eatingService.shutdown();
    }

    @AfterAll
    static void tearDown(){
        System.out.println("Stop test!");
    }

    @ParameterizedTest
    @CsvSource({"1, 1", "3, 4", "5, 7", "10, 15", "-5, 0", "29, 30"})
    void isNegativeNumberCanExistInCell(int number, int expected){
        when(randomService.randomNumber()).thenReturn(24);
        cell.addToCell(number, factory.create(AnimalType.WOLF));

        reproduceService.reproduceAll();

        long wolvesAfter = cell.getCurrentAnimalCount(AnimalType.WOLF);

        assertEquals(expected, wolvesAfter);
    }

    @Test
    void whenMaxInCellThrowsException(){
        cell.addToCell(31, factory.create(AnimalType.WOLF));

        assertThrows(MaxInCellException.class, () -> reproduceService.reproduceAll());
    }
}
