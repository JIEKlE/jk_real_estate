package jiekie.model;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public record RealEstateInventoryHolder(String name, int page) implements InventoryHolder {
    @Override
    public int page() {
        return page;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
