import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.javarush.iliasov.cryptoanalyzer.Cell;
import ru.javarush.iliasov.cryptoanalyzer.Island;
import ru.javarush.iliasov.cryptoanalyzer.Organisms.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MovementTest {
    private Cell[][] cell = new Cell()[20][100];
    private Island island = new Island(cell);
    private AnimalConfig config;
    private AnimalType type;
    private ConfigLoader loader;
    private Animal animal;

    @Test
    void testingMovementServiceReallyWorks(){
        AnimalFactory animalFactory = animal.getConfig(config);
        Mockito.when(island.move()).thenReturn(10);
        assertEquals()
    }
}
