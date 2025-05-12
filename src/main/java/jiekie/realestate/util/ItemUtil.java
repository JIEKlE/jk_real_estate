package jiekie.realestate.util;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtil {
    public static boolean compareContractWithTemplate(ItemStack a, ItemStack b) {
        if(a == null || b == null) return false;
        if(a.getType() != b.getType()) return false;

        ItemMeta metaA = a.getItemMeta();
        ItemMeta metaB = b.getItemMeta();

        if(metaA == null || metaB == null) return false;
        if(metaA.hasDisplayName() != metaB.hasDisplayName()) return false;
        if(metaA.hasDisplayName() && !metaA.getDisplayName().equals(metaB.getDisplayName())) return false;

        if(metaA.hasCustomModelData() != metaB.hasCustomModelData()) return false;
        if(metaA.hasCustomModelData() && metaA.getCustomModelData() != metaB.getCustomModelData()) return false;

        return metaA.hasLore() == metaB.hasLore();
    }

    public static boolean isContractName(ItemStack item) {
        if(item == null) return false;

        ItemMeta meta = item.getItemMeta();
        if(meta == null) return false;

        if(!meta.hasDisplayName()) return false;
        if(!meta.getDisplayName().startsWith(ChatColor.WHITE + ChatUtil.HOME_EMOJI)) return false;
        return meta.getDisplayName().endsWith("계약서");
    }

    public static String getRegionNameFromContract(ItemStack item) {
        String displayName = item.getItemMeta().getDisplayName();
        String regionName = displayName.substring(displayName.indexOf("[") + 1);
        regionName = regionName.substring(0, regionName.indexOf("]"));
        return regionName;
    }

    public static String getContractType(ItemStack item) {
        if(item.getItemMeta().hasEnchant(Enchantment.LUCK))
            return "판매";

        return "구매";
    }
}
