import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.javarush.iliasov.cryptoanalyzer.Cell;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.Animal;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.AnimalFactory;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.AnimalType;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.ConfigLoader;
import ru.javarush.iliasov.cryptoanalyzer.Services.DeathService;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class DeathTest {
    AnimalFactory factory;
    DeathService deathService;
    Cell cell;

    @BeforeEach
    void init() throws IOException {
        this.factory = new AnimalFactory(new ConfigLoader());
        this.deathService = new DeathService();
        this.cell = new Cell(new ArrayList<>(), new ArrayList<>());
    }

    @Test
    void whenDeadAnimalShoulBeRemovedFromCell(){
        Animal animal = factory.create(AnimalType.WOLF);
        animal.setAlive(false);
        cell.addAnimal(animal);
        deathService.death(animal, cell);
        assertFalse(cell.getAnimalsList().contains(animal));
    }

    @Test
    void whenAliveAnimalShouldNotBeRemovedFromCell(){
        Animal animal = factory.create(AnimalType.WOLF);
        animal.setAlive(true);
        cell.addAnimal(animal);

        deathService.death(animal, cell);
        assertTrue(cell.getAnimalsList().contains(animal));
    }
}
