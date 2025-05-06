package jiekie.event;

import jiekie.RealEstatePlugin;
import jiekie.exception.RealEstateException;
import jiekie.manager.RealEstateManager;
import jiekie.model.RealEstate;
import jiekie.model.RealEstateInventoryHolder;
import jiekie.util.ChatUtil;
import jiekie.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

public class GuiEvent implements Listener {
    private final RealEstatePlugin plugin;

    public GuiEvent(RealEstatePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        onRealEstateInventoryClick(e);
    }

    private void onRealEstateInventoryClick(InventoryClickEvent e) {
        HumanEntity humanEntity = e.getWhoClicked();
        if(!(humanEntity instanceof Player player)) return;

        Inventory inventory = e.getClickedInventory();
        if(inventory == null) return;
        if(!(inventory.getHolder() instanceof RealEstateInventoryHolder holder)) return;

        e.setCancelled(true);

        ItemStack item = e.getCurrentItem();
        if(item == null || item.getType() == Material.AIR) return;

        int slot = e.getSlot();
        int page = holder.page();
        RealEstateManager realEstateManager = plugin.getRealEstateManager();
        if(slot == 45) {
            realEstateManager.open(player, page - 1);
            return;
        }

        if(slot == 53) {
            realEstateManager.open(player, page + 1);
            return;
        }

        PlayerInventory playerInventory = player.getInventory();
        if(playerInventory.firstEmpty() == -1) {
            ChatUtil.showMessage(player, ChatUtil.INVENTORY_FULL);
            return;
        }

        try {
            String regionName = ItemUtil.getRegionNameFromContract(item);
            RealEstate realEstate = realEstateManager.getRealEstateOrThrow(regionName);
            ItemStack contract = plugin.getContractManager().getContract(realEstate);

            UUID ownerUuid = realEstate.getOwnerUuid();
            if(ownerUuid != null && ownerUuid.equals(player.getUniqueId()))
                playerInventory.addItem(contract);
            if(ownerUuid == null)
                playerInventory.addItem(contract);

        } catch (RealEstateException ex) {
            ChatUtil.showMessage(player, ex.getMessage());
        }

    }
}
