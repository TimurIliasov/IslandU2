import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.javarush.iliasov.cryptoanalyzer.Cell;
import ru.javarush.iliasov.cryptoanalyzer.Island;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.AnimalFactory;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.AnimalType;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.ConfigLoader;
import ru.javarush.iliasov.cryptoanalyzer.Services.InitializationService;
import ru.javarush.iliasov.cryptoanalyzer.Services.RandomService;
import ru.javarush.iliasov.cryptoanalyzer.Services.StatisticsService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StatisticTest {
    ConfigLoader configLoader;
    AnimalFactory factory;
    Island island;


    @BeforeEach
    void init() throws IOException {
        configLoader = new ConfigLoader();
        factory = new AnimalFactory(configLoader);
        island = new Island(new Cell[4][5]);
    }

    @Test
    void whenStatisticsCountsAnimalsByType() {
        for (int i = 0; i < island.getCells().length; i++) {
            for (int j = 0; j < island.getCells()[i].length; j++) {
                Cell cell = new Cell(new ArrayList<>(), new ArrayList<>());
                island.getCells()[i][j] = cell;
            }
        }

        island.getCells()[0][3].addAnimal(factory.create(AnimalType.WOLF));
        island.getCells()[3][3].addAnimal(factory.create(AnimalType.WOLF));
        island.getCells()[2][4].addAnimal(factory.create(AnimalType.WOLF));
        island.getCells()[3][1].addAnimal(factory.create(AnimalType.RABBIT));
        island.getCells()[1][4].addAnimal(factory.create(AnimalType.RABBIT));

        StatisticsService service = new StatisticsService(island);
        Map<AnimalType, Integer> animalTypeIntegerMap = service.getAnimalCount();
        assertEquals(3, animalTypeIntegerMap.get(AnimalType.WOLF));
        assertEquals(2, animalTypeIntegerMap.get(AnimalType.RABBIT));
    }

    @Test
    void whenIslandHasNoAnimalsStatisticsIsEmpty(){
        for (int i = 0; i < island.getCells().length; i++) {
            for (int j = 0; j < island.getCells()[i].length; j++) {
                Cell cell = new Cell(new ArrayList<>(), new ArrayList<>());
                island.getCells()[i][j] = cell;
            }
        }

        StatisticsService service = new StatisticsService(island);
        Map<AnimalType, Integer> animalTypeIntegerMap = service.getAnimalCount();
        assertTrue(animalTypeIntegerMap.isEmpty());
    }

    @Test
    void whenStatisticsCountPlants(){
        RandomService randomService = Mockito.mock(RandomService.class);
        Mockito.when(randomService.getRandomNumber(200)).thenReturn(12);
        InitializationService service = new InitializationService(randomService, configLoader, factory);
        service.cellPlant(island);
        StatisticsService statisticsService = new StatisticsService(island);
        int plantsNumber = statisticsService.getPlantsCount();
        assertEquals(20*12, plantsNumber);
    }
}

