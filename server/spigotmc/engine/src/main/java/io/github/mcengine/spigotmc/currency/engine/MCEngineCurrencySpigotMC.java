package io.github.mcengine.spigotmc.currency.engine;

import io.github.mcengine.api.currency.MCEngineCurrencyApi;
import io.github.mcengine.api.mcengine.MCEngineApi;
import io.github.mcengine.api.mcengine.Metrics;
import io.github.mcengine.common.currency.command.MCEngineCurrencyCommonCommand;
import io.github.mcengine.common.currency.tabcompleter.MCEngineCurrencyCommonTabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public class MCEngineCurrencySpigotMC extends JavaPlugin {
    /**
     * Called when the plugin is enabled.
     * Performs configuration loading, token validation, API initialization, and schedules token validation checks.
     */
    @Override
    public void onEnable() {
        new Metrics(this, 26077);
        saveDefaultConfig(); // Save config.yml if it doesn't exist

        boolean enabled = getConfig().getBoolean("enable", false);
        if (!enabled) {
            getLogger().warning("Plugin is disabled in config.yml (enable: false). Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        MCEngineCurrencyApi currencyApi = new MCEngineCurrencyApi(this);

        getCommand("currency").setExecutor(new MCEngineCurrencyCommonCommand(currencyApi));
        getCommand("currency").setTabCompleter(new MCEngineCurrencyCommonTabCompleter());

        // Load extensions
        MCEngineApi.loadExtensions(this, "addons", "AddOn");
        MCEngineApi.loadExtensions(this, "dlcs", "DLC");

        MCEngineApi.checkUpdate(this, "github", "MCEngine", "currency-engine", getConfig().getString("github.token", "null"));
    }

    /**
     * Called when the plugin is disabled.
     */
    @Override
    public void onDisable() {}
}
