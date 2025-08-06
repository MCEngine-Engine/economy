package io.github.mcengine.papermc.currency.engine;

import io.github.mcengine.api.core.MCEngineCoreApi;
import io.github.mcengine.api.core.Metrics;
import io.github.mcengine.common.currency.MCEngineCurrencyCommon;
import io.github.mcengine.common.currency.command.MCEngineCurrencyCommonCommand;
import io.github.mcengine.common.currency.tabcompleter.MCEngineCurrencyCommonTabCompleter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main PaperMC plugin class for MCEngineCurrency.
 */
public class MCEngineCurrencyPaperMC extends JavaPlugin {

    /**
     * Called when the plugin is enabled.
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

        // Initialize core currency API
        MCEngineCurrencyCommon api = new MCEngineCurrencyCommon(this);

        // Register command namespace and dispatcher
        String namespace = "currency";
        api.registerNamespace(namespace);
        api.registerSubCommand(namespace, "default", new MCEngineCurrencyCommonCommand(api));
        api.registerSubTabCompleter(namespace, "default", new MCEngineCurrencyCommonTabCompleter());

        // Assign dispatcher to command
        CommandExecutor dispatcher = api.getDispatcher(namespace);
        getCommand(namespace).setExecutor(dispatcher);
        getCommand(namespace).setTabCompleter((TabCompleter) dispatcher); // Dispatcher implements both interfaces

        // Load extensions
        MCEngineCoreApi.loadExtensions(
            this,
            "io.github.mcengine.api.currency.extension.library.IMCEngineCurrencyLibrary",
            "libraries",
            "Library"
        );
        MCEngineCoreApi.loadExtensions(
            this,
            "io.github.mcengine.api.currency.extension.api.IMCEngineCurrencyAPI",
            "apis",
            "API"
        );
        MCEngineCoreApi.loadExtensions(
            this,
            "io.github.mcengine.api.currency.extension.addon.IMCEngineCurrencyAddOn",
            "addons",
            "AddOn"
        );
        MCEngineCoreApi.loadExtensions(
            this,
            "io.github.mcengine.api.currency.extension.dlc.IMCEngineCurrencyDLC",
            "dlcs",
            "DLC"
        );

        // Check for plugin updates
        MCEngineCoreApi.checkUpdate(
            this,
            getLogger(),
            "github",
            "MCEngine",
            "currency-engine",
            getConfig().getString("github.token", "null")
        );
    }

    /**
     * Called when the plugin is disabled.
     */
    @Override
    public void onDisable() {
        // Plugin shutdown logic if needed
    }
}
