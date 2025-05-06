package jiekie.event;

import jiekie.RealEstatePlugin;
import jiekie.exception.RealEstateException;
import jiekie.manager.RealEstateManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerEvent implements Listener {
    private final RealEstatePlugin plugin;

    public PlayerEvent(RealEstatePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        setMaxOwnedCount(e.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

    }

    private void setMaxOwnedCount(Player player) {
        RealEstateManager realEstateManager = plugin.getRealEstateManager();
        if(realEstateManager.maxOwnedCountIsSet(player.getUniqueId())) return;
        try {
            realEstateManager.setMaxOwnedCount(player.getName(), "1");
        } catch (RealEstateException e) {
            Bukkit.getLogger().info(e.getMessage());
        }
    }
}
