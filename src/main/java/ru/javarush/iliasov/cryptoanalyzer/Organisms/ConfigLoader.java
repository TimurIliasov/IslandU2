package ru.javarush.iliasov.cryptoanalyzer.Organisms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class ConfigLoader {
    private final Map<AnimalType, AnimalConfig> configs;

    public ConfigLoader(Map<AnimalType, AnimalConfig> configs) {
        this.configs = configs;
    }


    public AnimalConfig getConfig(AnimalType type) {
        return configs.get(type);
    }


    public void readValue() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(this.getClass().getResource("/animals.json"));
        JsonNode animalsNode = jsonNode.get("animals");
        Iterator<Map.Entry<String, JsonNode>> iterator = animalsNode.fields();
        iterator.forEachRemaining(e -> {
            try {
                configs.put(AnimalType.valueOf(e.getKey()), mapper.treeToValue(e.getValue(), AnimalConfig.class));
            } catch (JsonProcessingException ex) {
                throw new RuntimeException("Failed to upload animal configuration" + e.getKey(), ex);
            }
        });

    }
}
