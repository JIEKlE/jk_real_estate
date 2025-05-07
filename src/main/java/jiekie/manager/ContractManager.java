package jiekie.manager;

import jiekie.RealEstatePlugin;
import jiekie.model.RealEstate;
import jiekie.util.ChatUtil;
import jiekie.util.NumberUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class ContractManager {
    private final RealEstatePlugin plugin;
    private final Map<String, ItemStack> contractMap = new HashMap<>();
    private final String CONTRACT_PREFIX = "contract";

    public ContractManager(RealEstatePlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        contractMap.clear();
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection contracts = config.getConfigurationSection(CONTRACT_PREFIX);

        if(contracts == null) return;
        for(String name : contracts.getKeys(false)) {
            try {
                String path = CONTRACT_PREFIX + "." + name;
                String encodedItem = config.getString(path);
                contractMap.put(name, itemFromBase64(encodedItem));
                
            } catch (IOException | ClassNotFoundException e) {
                plugin.getLogger().info("계약서 아이템 불러오기 실패");
            }
        }
    }

    public List<String> getTemplateNames() {
        return new ArrayList<>(contractMap.keySet());
    }

    public void registerTemplate(String name, ItemStack item) {
        ItemStack template = item.clone();
        template.setAmount(1);
        contractMap.put(name, template);
    }

    public void removeTemplate(String name) {
        contractMap.remove(name);
        plugin.getRealEstateManager().resetTemplate(name);
    }

    public boolean existTemplate(String name) {
        return contractMap.containsKey(name);
    }

    public ItemStack getContract(RealEstate realEstate) {
        ItemStack contract;
        String templateName = realEstate.getTemplateName();
        if(templateName == null || templateName.isBlank())
            contract = new ItemStack(Material.PAPER);
        else
            contract = contractMap.get(templateName).clone();

        String regionName = realEstate.getName();
        String price = NumberUtil.getFormattedMoney(realEstate.getPrice());
        String ownerName = realEstate.getOwnerName();

        ItemMeta meta = contract.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + ChatUtil.HOME_EMOJI + " [" + realEstate.getName() + "] 계약서");
        meta.setLore(getContractLore(regionName, price, ownerName));
        if(ownerName != null && !ownerName.isBlank()) {
            meta.addEnchant(Enchantment.LUCK, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        contract.setItemMeta(meta);

        return contract;
    }

    private static List<String> getContractLore(String regionName, String price, String ownerName) {
        List<String> lore = new ArrayList<>();

        // 부동산 상태
        lore.add(ChatColor.WHITE + "소재지 : " + regionName);
        lore.add(ChatColor.WHITE + "계약금 : " + price);
        if(ownerName == null || ownerName.isBlank())
            lore.add(ChatColor.WHITE + "판매상태 : " + ChatUtil.FOR_SALE_EMOJI);
        else
            lore.add(ChatColor.WHITE + "판매상태 : " + ChatUtil.SOLD_EMOJI);

        // 계약내용
        lore.add("");
        lore.add(ChatColor.RED + "계약내용");
        lore.add(ChatColor.WHITE + "제1조. 계약이 종료된 경우 위 부동산을 원상으로 회복하여 반환한다.");
        lore.add("");

        // 서명
        if(ownerName == null || ownerName.isBlank())
            lore.add(ChatColor.YELLOW + "서명 : " + ChatColor.WHITE + "이름");
        else
            lore.add(ChatColor.YELLOW + "서명 : " + ChatColor.WHITE + ownerName);

        return lore;
    }

    public void save() {
        FileConfiguration config = plugin.getConfig();
        config.set(CONTRACT_PREFIX, null);

        for(Map.Entry<String, ItemStack> entry : contractMap.entrySet()) {
            try {
                String name = entry.getKey();
                ItemStack template = entry.getValue();

                String path = CONTRACT_PREFIX + "." + name;
                config.set(path, itemStackToBase64(template));

            } catch (IOException e) {
                plugin.getLogger().info("계약서 아이템 저장 실패");
            }
        }

        plugin.saveConfig();
    }

    private String itemStackToBase64(ItemStack item) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

        dataOutput.writeObject(item);
        dataOutput.close();
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    private ItemStack itemFromBase64(String base64) throws IOException, ClassNotFoundException {
        byte[] data = Base64.getDecoder().decode(base64);
        BukkitObjectInputStream inputStream = new BukkitObjectInputStream(new ByteArrayInputStream(data));
        ItemStack item = (ItemStack) inputStream.readObject();
        inputStream.close();
        return item;
    }
}
