import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.junit.jupiter.api.Test;
import ru.javarush.iliasov.islandproject.Island;
import ru.javarush.iliasov.islandproject.Organisms.AnimalFactory;
import ru.javarush.iliasov.islandproject.Services.SimulationService;
import ru.javarush.iliasov.islandproject.controll.UI;

import static org.fusesource.jansi.Ansi.ansi;

public class UITes {
    Island island;
    SimulationService simulationService;



    @Test
    void whenANSIWorks(){
        System.setProperty("jansi.mode", "force");
        AnsiConsole.systemInstall();
        AnsiConsole.out().print("AAAA");

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        AnsiConsole.out().print(ansi().cursor(2, 1));

        AnsiConsole.out().flush();

        AnsiConsole.out().print("BBBB");

        AnsiConsole.out().flush();

        AnsiConsole.systemUninstall();
    }
}
