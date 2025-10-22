package io.github.mcengine.papermc.economy.engine;

import io.github.mcengine.api.core.MCEngineCoreApi;
import io.github.mcengine.api.core.Metrics;
import io.github.mcengine.common.economy.MCEngineEconomyCommon;
import io.github.mcengine.common.economy.command.MCEngineEconomyCommonCommand;
import io.github.mcengine.common.economy.tabcompleter.MCEngineEconomyCommonTabCompleter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main PaperMC plugin class for the MCEngine Economy module.
 */
public class MCEngineEconomyPaperMC extends JavaPlugin {

    /** Metrics project id for bStats. */
    private static final int METRICS_PROJECT_ID = 22562;

    /** Command root namespace for this module. */
    private static final String COMMAND_NAMESPACE = "economy";

    /**
     * Called when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        new Metrics(this, METRICS_PROJECT_ID);
        saveDefaultConfig(); // Save config.yml if it doesn't exist

        boolean enabled = getConfig().getBoolean("enable", false);
        if (!enabled) {
            getLogger().warning("Plugin is disabled in config.yml (enable: false). Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        String license = getConfig().getString("licenses.license", "free"); 
        if (!license.equalsIgnoreCase("free")) { 
            getLogger().warning("Plugin is disabled in config.yml.");
            getLogger().warning("Invalid license.");
            getLogger().warning("Check license or use \"free\".");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize core economy API
        MCEngineEconomyCommon api = new MCEngineEconomyCommon(this);

        // Register command namespace and dispatcher
        api.registerNamespace(COMMAND_NAMESPACE);
        api.registerSubCommand(COMMAND_NAMESPACE, "default", new MCEngineEconomyCommonCommand(api));
        api.registerSubTabCompleter(COMMAND_NAMESPACE, "default", new MCEngineEconomyCommonTabCompleter());

        // Assign dispatcher to command
        CommandExecutor dispatcher = api.getDispatcher(COMMAND_NAMESPACE);
        getCommand(COMMAND_NAMESPACE).setExecutor(dispatcher);
        getCommand(COMMAND_NAMESPACE).setTabCompleter((TabCompleter) dispatcher); // Dispatcher implements both interfaces

        // Load extensions (Libraries, APIs, Agents, AddOns, DLCs)
        MCEngineCoreApi.loadExtensions(
            this,
            "io.github.mcengine.api.economy.extension.library.IMCEngineEconomyLibrary",
            "libraries",
            "Library"
        );
        MCEngineCoreApi.loadExtensions(
            this,
            "io.github.mcengine.api.economy.extension.api.IMCEngineEconomyAPI",
            "apis",
            "API"
        );
        MCEngineCoreApi.loadExtensions(
            this,
            "io.github.mcengine.api.economy.extension.agent.IMCEngineEconomyAgent",
            "agents",
            "Agent"
        );
        MCEngineCoreApi.loadExtensions(
            this,
            "io.github.mcengine.api.economy.extension.addon.IMCEngineEconomyAddOn",
            "addons",
            "AddOn"
        );
        MCEngineCoreApi.loadExtensions(
            this,
            "io.github.mcengine.api.economy.extension.dlc.IMCEngineEconomyDLC",
            "dlcs",
            "DLC"
        );

        // Check for plugin updates (repo names updated to Economy)
        MCEngineCoreApi.checkUpdate(
            this,
            getLogger(),
            "github",
            "MCEngine-Engine",
            "economy",
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
