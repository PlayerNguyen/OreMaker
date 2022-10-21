package com.github.playernguyen.oremaker.configurations;

import com.google.common.collect.Maps;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PluginConfiguration {

    public enum PluginConfigurationItem {
        GENERATOR_DISTRIBUTION ("generator.distribute",
                List.of(String.format("%s %.2f", Material.STONE, 8.0D),
                        String.format("%s %.2f", Material.IRON_ORE, 3.0D),
                        String.format("%s %.2f", Material.GOLD_ORE, .2F),
                        String.format("%s %.2f", Material.DIAMOND_ORE, .02F),
                        String.format("%s %.2f", Material.EMERALD_ORE, .05F)

                )
        );

        private final String key;
        private final Object defaultValue;
        PluginConfigurationItem(String key, Object defaultVal) {
            this.key = key;
            this.defaultValue = defaultVal;
        }

        public String getKey() {
            return key;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }
    }

    public static final String CONFIGURATION_FILE_NAME = "config.yml";

    private final File file;
    private final YamlConfiguration configuration;

    public PluginConfiguration(Plugin plugin) throws IOException {
        // Load file configuration
        this.file = new File(plugin.getDataFolder(), CONFIGURATION_FILE_NAME);

        // Load configuration
        this.configuration = YamlConfiguration.loadConfiguration(this.file);

        // Load default if not exists
        for (PluginConfigurationItem value : PluginConfigurationItem.values()) {
            if (!configuration.contains(value.key)) {
                this.configuration.set(value.key, value.defaultValue);
            }
        }

        // Save the configuration
        this.configuration.save(this.file);
    }

    public HashMap<Material, Double> getGeneratorDistribution() throws IOException {
        List<String> result  = this.configuration
                .getStringList(PluginConfigurationItem.GENERATOR_DISTRIBUTION.key);

        HashMap<Material, Double> dummy = new HashMap<>();
        for (String s : result) {
            String[] substring = s.split(" ");

            // Invalid format
            if (substring.length < 2) {
                throw new IOException(String.format("Invalid distribute format %s", s));
            }

            // Invalid material name
            Material material = Material.getMaterial(substring[0]);
            if (material == null) {
                throw new NullPointerException(String.format("Invalid material %s", substring[0]));
            }

            // Random value must be a float must positive
            try {
                double v = Double.parseDouble(substring[1]);
                dummy.put(material, v);
            } catch (NumberFormatException e) {
                throw new IllegalStateException(String.format("The value must be a float number %s", substring[1]));
            }
        }

        return dummy;
    }

}
