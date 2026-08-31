import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.javarush.iliasov.cryptoanalyzer.Cell;
import ru.javarush.iliasov.cryptoanalyzer.Island;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.*;
import ru.javarush.iliasov.cryptoanalyzer.Services.EatingService;
import ru.javarush.iliasov.cryptoanalyzer.Services.RandomService;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class EatTest {
    ConfigLoader configLoader;
    AnimalFactory factory;
    Cell cell;
    Animal hunter;
    Animal prey;
    EatingService eatingService;
    Island island;
    @BeforeEach
    void init() throws IOException {
        island = new Island(new Cell[5][5]);
        configLoader = new ConfigLoader();
        factory = new AnimalFactory(configLoader);
        hunter = factory.create(AnimalType.WOLF);
        prey = factory.create(AnimalType.RABBIT);
        eatingService = new EatingService(island);
        for (int i = 0; i < island.getCells().length; i++) {
            for (int j = 0; j < island.getCells()[i].length; j++) {
                island.getCells()[i][j] = new Cell(new ArrayList<>(), new ArrayList<>());
            }
        }

        cell = island.getCells()[2][2];
    }
    @Test
    void whenAnimalEatsAnimalGainsWeight()  {
        RandomService randomService =   mock(RandomService.class);
        when(randomService.randomNumber()).thenReturn(24);
        cell.addAnimal(hunter);
        cell.addAnimal(prey);

        double hunterWeight = hunter.getWeight();
        double preyWeight = prey.getWeight();

        eatingService.eatAll(randomService);
        assertFalse(prey.getAlive());
        assertEquals(hunterWeight + preyWeight, hunter.getWeight());
    }

    @Test
    void whenHunterCouldNotEatPrey(){
        cell.addAnimal(hunter);
        cell.addAnimal(prey);
        RandomService randomService = mock(RandomService.class);
        when(randomService.randomNumber()).thenReturn(78);

        double hunterWeight = hunter.getWeight();

        eatingService.eatAll(randomService);
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
}

