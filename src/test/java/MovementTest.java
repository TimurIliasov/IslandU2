import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.javarush.iliasov.cryptoanalyzer.Cell;
import ru.javarush.iliasov.cryptoanalyzer.Island;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.*;
import ru.javarush.iliasov.cryptoanalyzer.Services.MovementService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static java.lang.Character.getType;
import static org.junit.jupiter.api.Assertions.*;
import static ru.javarush.iliasov.cryptoanalyzer.Organisms.AnimalType.WOLF;

public class MovementTest {
    private Island island = new Island(new Cell[5][5]);

    @Test
    void testingMovementServiceReallyWorks() throws IOException {

        for (int i = 0; i < island.getCells().length; i++) {
            for (int j = 0; j < island.getCells()[i].length; j++) {
                Cell cell = new Cell(new ArrayList<>(), new ArrayList<>());
                island.getCells()[i][j] = cell;
            }
        }

        ConfigLoader configLoader = new ConfigLoader();
        AnimalFactory factory = new AnimalFactory(configLoader);
        Animal animal = factory.create(WOLF);
        island.getCells()[0][0].addAnimal(animal);


        MovementService movementService = new MovementService(island);
        movementService.move();

        assertFalse(island.getCells()[0][0].getAnimalsList().contains(
                animal));

        boolean findedMovement = false;
        for (int i = 0; i < island.getCells().length; i++) {
            for (int j = 0; j < island.getCells()[i].length; j++) {
                if (island.getCells()[i][j].getAnimalsList().contains(animal)) {
                    findedMovement = true;
                    break;
                }
            }
            if (findedMovement){
                break;
            }
        }
        assertTrue(findedMovement);
    }
}
