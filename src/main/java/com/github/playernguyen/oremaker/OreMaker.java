package com.github.playernguyen.oremaker;

import com.github.playernguyen.oremaker.configurations.PluginConfiguration;
import com.github.playernguyen.oremaker.utils.RandomPipe;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public final class OreMaker extends JavaPlugin {
    private static OreMaker plugin;

    private PluginConfiguration pluginConfiguration;
    private RandomPipe<Material> materialRandomPipe;


    @Override
    public void onEnable() {
        plugin = this;
        try {
            setupConfig();
            setupRandomPipe();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupConfig() throws IOException {
        // setup data folder before load configuration
        File dataFolder = this.getDataFolder();
        // Load if the data folder not exists
        if (!(dataFolder.exists() && dataFolder.mkdirs())) {
            throw new IllegalStateException("Cannot setup data folder");
        }

        // Then load the plugin configuration
        this.pluginConfiguration = (this.pluginConfiguration != null
                ? this.pluginConfiguration
                : new PluginConfiguration(this));

    }

    private void setupRandomPipe() throws IOException {
        // Load the random pipe from configuration
        HashMap<Material, Double> generatorDistribution =
                this.pluginConfiguration.getGeneratorDistribution();

        this.materialRandomPipe = RandomPipe.from(generatorDistribution);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public RandomPipe<Material> getMaterialRandomPipe() {
        return materialRandomPipe;
    }
}
