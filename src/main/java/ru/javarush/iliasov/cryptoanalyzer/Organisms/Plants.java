package ru.javarush.iliasov.cryptoanalyzer.Organisms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Plants {
    private final double weight = 1;
    private final int maxInCell = 200;

    public Plants(){

    }

    public List<Plants> plantsInCell(){
        List<Plants> plants = new ArrayList<>();
        int min = 0;
        int randomCell = ThreadLocalRandom.current().nextInt(min, maxInCell+1);
        for (int i = 0; i < randomCell; i++) {
            plants.add(new Plants());
        }
        return plants;
    }

}
