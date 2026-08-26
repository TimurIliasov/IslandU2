import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.javarush.iliasov.cryptoanalyzer.Cell;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.*;
import ru.javarush.iliasov.cryptoanalyzer.Services.EatingService;
import ru.javarush.iliasov.cryptoanalyzer.Services.RandomService;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class EatTest {
    ConfigLoader configLoader;
    AnimalFactory factory;
    Cell cell;
    Animal hunter;
    Animal prey;
    EatingService eatingService;
    @BeforeEach
    void init() throws IOException {
        cell = new Cell(new ArrayList<>(), new ArrayList<>());
        configLoader = new ConfigLoader();
        factory = new AnimalFactory(configLoader);
        hunter = factory.create(AnimalType.WOLF);
        prey = factory.create(AnimalType.RABBIT);
        eatingService = new EatingService(cell);
    }
    @Test
    void whenAnimalEatsAnimalGainsWeight() throws IOException {
        RandomService randomService = Mockito.mock(RandomService.class);
        Mockito.when(randomService.randomNumber()).thenReturn(24);
        cell.addAnimal(hunter);
        cell.addAnimal(prey);

        double hunterWeight = hunter.getWeight();
        double preyWeight = prey.getWeight();

        eatingService.eat(prey, hunter, randomService);
        assertFalse(prey.getAlive());
        assertEquals(hunterWeight + preyWeight, hunter.getWeight());
    }

    @Test
    void whenHunterCouldNotEatPrey(){
        cell.addAnimal(hunter);
        cell.addAnimal(prey);
        RandomService randomService = Mockito.mock(RandomService.class);
        Mockito.when(randomService.randomNumber()).thenReturn(78);

        double hunterWeight = hunter.getWeight();
        double preyWeight = prey.getWeight();

        eatingService.eat(prey, hunter, randomService);
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

        eatingService.eatPlant(prey, plants);
        assertEquals(preyWeight + plantWeight, prey.getWeight());
        assertFalse(cell.getPlantsList().contains(plants));
    }
}

