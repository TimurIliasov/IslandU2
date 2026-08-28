package ru.javarush.iliasov.cryptoanalyzer.Services;

import ru.javarush.iliasov.cryptoanalyzer.Organisms.Plants;

import java.util.concurrent.ThreadLocalRandom;

public class RandomService {
    public int randomNumber(){
        return ThreadLocalRandom.current().nextInt(100);
    }

    public int getRandomNumber(int maxInCell){
        return  ThreadLocalRandom.current().nextInt(maxInCell + 1);
    }
}
