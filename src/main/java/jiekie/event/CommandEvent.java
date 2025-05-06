package jiekie.event;

import jiekie.RealEstatePlugin;
import jiekie.exception.RealEstateException;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandEvent implements Listener {
    private final RealEstatePlugin plugin;

    public CommandEvent(RealEstatePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        String message = e.getMessage().toLowerCase();

        if(message.startsWith("/region remove") || message.startsWith("/rg remove") || message.startsWith("/worldguard:region remove") || message.startsWith("/worldguard:rg remove")) {
            String[] args = message.split("\\s+");
            if(args.length < 3) return;

            String regionName = args[2];
            try {
                plugin.getRealEstateManager().removeRegion(regionName);
            } catch (RealEstateException ex) {
                Bukkit.getLogger().info(ex.getMessage());
            }
        }
    }
}
