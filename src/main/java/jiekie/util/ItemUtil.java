package jiekie.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtil {
    public static boolean isSameItem(ItemStack a, ItemStack b) {
        if(a == null || b == null) return false;
        if(a.getType() != b.getType()) return false;

        ItemMeta metaA = a.getItemMeta();
        ItemMeta metaB = b.getItemMeta();

        if(metaA == null && metaB == null) return true;
        if(metaA == null || metaB == null) return false;

        if(metaA.hasDisplayName() != metaB.hasDisplayName()) return false;
        if(metaA.hasDisplayName() && !metaA.getDisplayName().equals(metaB.getDisplayName())) return false;

        if(metaA.hasCustomModelData() != metaB.hasCustomModelData()) return false;
        if(metaA.hasCustomModelData() && metaA.getCustomModelData() != metaB.getCustomModelData()) return false;

        if(metaA.hasLore() != metaB.hasLore()) return false;
        if(metaA.hasLore() && !metaA.getLore().equals(metaB.getLore())) return false;

        if(!(metaA instanceof Damageable) || !(metaB instanceof Damageable)) return true;
        Damageable damageableA = (Damageable) metaA;
        Damageable damageableB = (Damageable) metaB;
        return damageableA.getDamage() == damageableB.getDamage();
    }
}
