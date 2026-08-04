package ru.javarush.iliasov.cryptoanalyzer.Organisms;

import java.util.Map;

public class ConfigLoader {
    private final Map<AnimalType, AnimalConfig> configs;

    public ConfigLoader(Map<AnimalType, AnimalConfig> configs) {
        this.configs = Map.copyOf(configs);
    }


    public AnimalConfig getConfig(AnimalType type){
        return configs.get(type);
    }
}
