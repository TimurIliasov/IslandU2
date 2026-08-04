package ru.javarush.iliasov.cryptoanalyzer.Organisms;

import java.util.Collections;
import java.util.Map;

public class AnimalConfig {
    private final int speed;
    private final int foodNeeded;
    private final Map<AnimalType, Integer> huntingProbability;
    private final int maxInCell;
    private final int startWeight;

    public AnimalConfig(int speed, int foodNeeded, Map<AnimalType, Integer> huntingProbability, int maxInCell, int startWeight) {
        this.speed = speed;
        this.foodNeeded = foodNeeded;
        this.huntingProbability = Map.copyOf(huntingProbability);
        this.maxInCell = maxInCell;
        this.startWeight = startWeight;
    }

    public int getSpeed(){
        return speed;
    }

    public int getFoodNeeded(){
        return foodNeeded;
    }

    public int getHuntingProbability(AnimalType type){
        return huntingProbability.getOrDefault(type, 0);
    }

    public Map<AnimalType, Integer> getHuntingProbabilities(){
        return huntingProbability;
    }

    public int getMaxInCell(){
        return maxInCell;
    }

    public int getStartWeight(){
        return startWeight;
    }

}
