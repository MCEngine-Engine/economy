package io.github.mcengine.papermc.currency.engine;

import io.github.mcengine.api.currency.MCEngineCurrencyApi;
import io.github.mcengine.api.mcengine.MCEngineApi;
import io.github.mcengine.api.mcengine.Metrics;
import io.github.mcengine.common.currency.command.MCEngineCurrencyCommonCommand;
import io.github.mcengine.common.currency.tabcompleter.MCEngineCurrencyCommonTabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public class MCEngineCurrencyPaperMC extends JavaPlugin {
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
        MCEngineApi.loadExtensions(
            this,
            "io.github.mcengine.api.currency.extension.addon.IMCEngineCurrencyLibrary",
            "libraries",
            "Library"
            );
        MCEngineApi.loadExtensions(
            this,
            "io.github.mcengine.api.currency.extension.dlc.IMCEngineCurrencyAPI",
            "apis",
            "API"
            );
        MCEngineApi.loadExtensions(
            this,
            "io.github.mcengine.api.currency.extension.addon.IMCEngineCurrencyAddOn",
            "addons",
            "AddOn"
            );
        MCEngineApi.loadExtensions(
            this,
            "io.github.mcengine.api.currency.extension.dlc.IMCEngineCurrencyDLC",
            "dlcs",
            "DLC"
            );

        MCEngineApi.checkUpdate(this, getLogger(), "github", "MCEngine", "currency-engine", getConfig().getString("github.token", "null"));
    }

    /**
     * Called when the plugin is disabled.
     */
    @Override
    public void onDisable() {}
}
