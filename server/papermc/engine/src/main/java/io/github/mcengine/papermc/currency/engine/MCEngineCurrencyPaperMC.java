package io.github.mcengine.papermc.currency.engine;

import io.github.mcengine.api.core.MCEngineApi;
import io.github.mcengine.api.core.Metrics;
import io.github.mcengine.common.currency.MCEngineCurrencyCommon;
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

        MCEngineCurrencyCommon currencyApi = new MCEngineCurrencyCommon(this);

        getCommand("currency").setExecutor(new MCEngineCurrencyCommonCommand(currencyApi));
        getCommand("currency").setTabCompleter(new MCEngineCurrencyCommonTabCompleter());

        // Load extensions
        MCEngineApi.loadExtensions(
            this,
            "io.github.mcengine.api.currency.extension.library.IMCEngineCurrencyLibrary",
            "libraries",
            "Library"
            );
        MCEngineApi.loadExtensions(
            this,
            "io.github.mcengine.api.currency.extension.api.IMCEngineCurrencyAPI",
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
