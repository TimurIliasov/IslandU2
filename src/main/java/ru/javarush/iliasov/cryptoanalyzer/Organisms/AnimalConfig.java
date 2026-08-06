package ru.javarush.iliasov.cryptoanalyzer.Organisms;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


import java.util.Map;

public class AnimalConfig {
    private final int speed;
    private final double foodNeeded;
    private final Map<AnimalType, Integer> huntingProbability;
    private final int maxInCell;
    private final double startWeight;

    @JsonCreator
    public AnimalConfig(@JsonProperty("speed") int speed,@JsonProperty("foodNeeded") double foodNeeded, @JsonProperty("huntingProbability") Map<AnimalType, Integer> huntingProbability, @JsonProperty("maxInCell") int maxInCell, @JsonProperty("startWeight") double startWeight) {
        this.speed = speed;
        this.foodNeeded = foodNeeded;
        this.huntingProbability = Map.copyOf(huntingProbability);
        this.maxInCell = maxInCell;
        this.startWeight = startWeight;
    }

    public int getSpeed(){
        return speed;
    }

    public double getFoodNeeded(){
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

    public double getStartWeight(){
        return startWeight;
    }

}
