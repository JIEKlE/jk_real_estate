package jiekie.realestate;

import jiekie.realestate.command.ContractCommand;
import jiekie.realestate.command.RealEstateCommand;
import jiekie.realestate.completer.ContractTabCompleter;
import jiekie.realestate.completer.RealEstateTabCompleter;
import jiekie.realestate.event.GuiEvent;
import jiekie.realestate.event.PlayerEvent;
import jiekie.realestate.event.CommandEvent;
import jiekie.realestate.manager.ContractManager;
import jiekie.realestate.manager.RealEstateManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class RealEstatePlugin extends JavaPlugin {
    private RealEstateManager realEstateManager;
    private ContractManager contractManager;

    @Override
    public void onEnable() {
        // config
        saveDefaultConfig();
        reloadConfig();

        // manager
        realEstateManager = new RealEstateManager(this);
        realEstateManager.load();
        contractManager = new ContractManager(this);
        contractManager.load();

        // event
        getServer().getPluginManager().registerEvents(new PlayerEvent(this), this);
        getServer().getPluginManager().registerEvents(new CommandEvent(this), this);
        getServer().getPluginManager().registerEvents(new GuiEvent(this), this);

        // command
        getCommand("부동산").setExecutor(new RealEstateCommand(this));
        getCommand("계약서").setExecutor(new ContractCommand(this));

        // tab completer
        getCommand("부동산").setTabCompleter(new RealEstateTabCompleter(this));
        getCommand("계약서").setTabCompleter(new ContractTabCompleter(this));

        getLogger().info("부동산 플러그인 by Jiekie");
        getLogger().info("Copyright © 2025 Jiekie. All rights reserved.");
    }

    public RealEstateManager getRealEstateManager() {
        return realEstateManager;
    }

    public ContractManager getContractManager() {
        return contractManager;
    }

    @Override
    public void onDisable() {
        realEstateManager.save();
        contractManager.save();
    }
}
