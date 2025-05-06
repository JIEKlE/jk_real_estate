package jiekie.manager;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import jiekie.RealEstatePlugin;
import jiekie.api.NicknameAPI;
import jiekie.exception.RealEstateException;
import jiekie.model.RealEstate;
import jiekie.util.ChatUtil;
import jiekie.util.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RealEstateManager {
    private final RealEstatePlugin plugin;
    private final Map<String, RealEstate> realEstateMap = new HashMap<>();
    private final Map<UUID, Integer> maxOwnedCountMap = new HashMap<>();
    private final String CONFIG_FILE_NAME = "real_estate.yml";
    private final String REAL_ESTATE_PREFIX = "real_estate";
    private final String MAX_OWNED_COUNT_PREFIX = "max_owned_count";

    public RealEstateManager(RealEstatePlugin plugin) {
        this.plugin = plugin;
    }

    /* load */
    public void load() {
        loadMaxOwnedCount();
        loadRealEstate();
    }

    private void loadMaxOwnedCount() {
        maxOwnedCountMap.clear();
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection section = config.getConfigurationSection(MAX_OWNED_COUNT_PREFIX);

        if(section == null) return;
        for(String uuidString : section.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidString);
            int count = section.getInt(uuidString);
            maxOwnedCountMap.put(uuid, count);
        }
    }

    private void loadRealEstate() {
        makeRealEstateFile();

        realEstateMap.clear();
        File file = new File(plugin.getDataFolder(), CONFIG_FILE_NAME);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection(REAL_ESTATE_PREFIX);

        if(section == null) return;
        for(String name : section.getKeys(false)) {
            String worldName = section.getString(name + ".world_name", null);
            int price = section.getInt(name + ".price", 0);
            RealEstate realEstate = new RealEstate(name, worldName, price);

            String ownerUuid = section.getString(name + ".owner_uuid", null);
            if(ownerUuid != null)
                realEstate.setOwnerUuid(UUID.fromString(ownerUuid));

            realEstate.setOwnerName(section.getString(name + ".owner_name", null));
            realEstate.setTemplateName(section.getString(name + ".template_name", null));
            realEstate.setMaxChestCount(section.getInt(name + ".max_chest_count", 10));
            realEstate.setMaxFurnaceCount(section.getInt(name + ".max_furnace_count", 10));

            realEstateMap.put(name, realEstate);
        }
    }

    private void makeRealEstateFile() {
        File file = new File(plugin.getDataFolder(), CONFIG_FILE_NAME);
        if(file.exists()) return;
        plugin.saveResource(CONFIG_FILE_NAME, false);
    }

    /* function */
    public void setRegion(World world, String name, String priceString) throws RealEstateException {
        if(!isRegionExist(name))
            createRegion(world, name, priceString);
        else
            updateRegion(world, name, priceString);
    }

    public boolean isRegionExist(String name) {
        return realEstateMap.containsKey(name);
    }

    private void createRegion(World world, String name, String priceString) throws RealEstateException {
        if(!isRegionExistInWorldGuard(world, name))
            throw new RealEstateException(ChatUtil.NOT_WORLD_GUARD_REGION);

        RealEstate realEstate = new RealEstate(name, world.getName(), getMoney(priceString));
        realEstateMap.put(name, realEstate);

        String prefix = "rg flag " + name + " ";
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), prefix + "use allow");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), prefix + "build deny");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), prefix + "chest-access deny");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), prefix + "interact deny");
    }

    private void updateRegion(World world, String name, String priceString) throws RealEstateException {
        RealEstate realEstate = getRealEstateOrThrow(name);
        if(!realEstate.getWorldName().equals(world.getName()))
            throw new RealEstateException(ChatUtil.DUPLICATED_REGION_NAME);

        if(!isRegionExistInWorldGuard(world, name)) {
            removeRegion(name);
            throw new RealEstateException(ChatUtil.NOT_WORLD_GUARD_REGION);
        }

        realEstate.setPrice(getMoney(priceString));
    }

    private boolean isRegionExistInWorldGuard(World world, String name) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(world));
        if(regionManager == null) return false;
        return regionManager.getRegion(name) != null;
    }

    public void removeRegion(String name) throws RealEstateException {
        getRealEstateOrThrow(name);
        realEstateMap.remove(name);
    }

    public void giveRegion(String name, Player player, String playerName) throws RealEstateException {
        if(!canOwnMoreRealEstate(player.getUniqueId()))
            throw new RealEstateException(ChatUtil.PLAYER_CAN_NOT_OWN_MORE_REAL_ESTATE);

        RealEstate realEstate = getRealEstateOrThrow(name);
        
        // take permission
        UUID beforeOwnerUuid = realEstate.getOwnerUuid();
        if(beforeOwnerUuid != null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(beforeOwnerUuid);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rg removeowner " + name + " " + offlinePlayer.getName());

            // feedback
            Player beforeOwner = Bukkit.getPlayer(beforeOwnerUuid);
            if(beforeOwner != null) {
                ChatUtil.regionIsTaken(beforeOwner, name);
                SoundUtil.playNoteBlockBell(beforeOwner);
            }
        }

        // give permission
        realEstate.setOwnerUuid(player.getUniqueId());
        realEstate.setOwnerName(playerName);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rg addowner " + name + " " + player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rg flag " + name + " use deny");
        ChatUtil.regionIsGiven(player, name);
        SoundUtil.playNoteBlockBell(player);
    }

    public void takeRegion(String name) throws RealEstateException {
        RealEstate realEstate = getRealEstateOrThrow(name);
        UUID ownerUuid = realEstate.getOwnerUuid();
        if(ownerUuid == null)
            throw new RealEstateException(ChatUtil.NO_OWNER);

        // take permission
        realEstate.setOwnerUuid(null);
        realEstate.setOwnerName(null);

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(ownerUuid);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rg removeowner " + name + " " + offlinePlayer.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rg flag " + name + " use allow");

        Player player = Bukkit.getPlayer(ownerUuid);
        if(player != null) {
            ChatUtil.regionIsTaken(player, name);
            SoundUtil.playNoteBlockBell(player);
        }
    }

    public void showOwnRegions(CommandSender sender, Player player, String playerName) {
        Integer count = maxOwnedCountMap.getOrDefault(player.getUniqueId(), 0);
        ChatUtil.myRealEstateInfoPrefix(sender, playerName, count);

        for(RealEstate realEstate : realEstateMap.values()) {
            UUID ownerUuid = realEstate.getOwnerUuid();
            if(ownerUuid == null) continue;
            if(ownerUuid.equals(player.getUniqueId()))
                ChatUtil.myRealEstateInfo(sender, realEstate);
        }

        ChatUtil.horizontalLineSuffix(sender);
    }

    public void setMaxChestCount(String name, String countString) throws RealEstateException {
        RealEstate realEstate = getRealEstateOrThrow(name);
        int count = getCount(countString);
        realEstate.setMaxChestCount(count);
    }

    public void setMaxFurnaceCount(String name, String countString) throws RealEstateException {
        RealEstate realEstate = getRealEstateOrThrow(name);
        int count = getCount(countString);
        realEstate.setMaxFurnaceCount(count);
    }

    public RealEstate getRealEstateOrThrow(String name) throws RealEstateException {
        if(!realEstateMap.containsKey(name))
            throw new RealEstateException(ChatUtil.REGION_NOT_FOUND);

        RealEstate realEstate = realEstateMap.get(name);
        if(realEstate == null)
            throw new RealEstateException(ChatUtil.REGION_NOT_FOUND);

        return realEstate;
    }

    private int getMoney(String moneyString) throws RealEstateException {
        int money;
        try {
            money = Integer.parseInt(moneyString);
        } catch (NumberFormatException e) {
            throw new RealEstateException(ChatUtil.MONEY_NOT_NUMBER);
        }

        if(money < 0)
            throw new RealEstateException(ChatUtil.MINUS_MONEY);

        return money;
    }

    private int getCount(String countString) throws RealEstateException {
        int count;
        try {
            count = Integer.parseInt(countString);
        } catch (NumberFormatException e) {
            throw new RealEstateException(ChatUtil.MAX_OWNED_COUNT_NOT_NUMBER);
        }

        if(count < 0)
            throw new RealEstateException(ChatUtil.MINUS_MAX_OWNED_COUNT);

        return count;
    }

    public void setTemplate(String name, String templateName) throws RealEstateException {
        RealEstate realEstate = getRealEstateOrThrow(name);
        realEstate.setTemplateName(templateName);
    }

    public void resetTemplate(String templateName) {
        for(RealEstate realEstate : realEstateMap.values()) {
            String template = realEstate.getTemplateName();
            if(template == null || template.isBlank()) continue;
            if(template.equals(templateName))
                realEstate.setTemplateName(null);
        }
    }

    /* tab completer */
    public List<String> getRealEstateNames() {
        if(realEstateMap.isEmpty()) return Collections.emptyList();
        return new ArrayList<>(realEstateMap.keySet());
    }

    public List<String> getWorldGuardRegionNames(World world) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(world));

        if(regionManager == null) return Collections.emptyList();
        return new ArrayList<>(regionManager.getRegions().keySet());
    }

    /* player max own count */
    public boolean maxOwnedCountIsSet(UUID uuid) {
        return maxOwnedCountMap.containsKey(uuid);
    }

    public boolean canOwnMoreRealEstate(UUID uuid) {
        Integer maxCount = maxOwnedCountMap.getOrDefault(uuid, 0);
        int ownedCount = 0;

        for(RealEstate realEstate : realEstateMap.values()) {
            UUID ownerUuid = realEstate.getOwnerUuid();
            if(ownerUuid == null) continue;
            if(ownerUuid.equals(uuid))
                ownedCount++;
        }

        return ownedCount < maxCount;
    }

    public void setMaxOwnedCount(String playerName, String countString) throws RealEstateException {
        Player player = NicknameAPI.getInstance().getPlayerByNameOrNickname(playerName);
        if(player == null)
            throw new RealEstateException(ChatUtil.PLAYER_DOES_NOT_EXIST);

        int count = getCount(countString);
        maxOwnedCountMap.put(player.getUniqueId(), count);
    }

    /* save */
    public void save() {
        saveMaxOwnedCount();
        saveRealEstate();
        plugin.saveConfig();
    }

    private void saveMaxOwnedCount() {
        FileConfiguration config = plugin.getConfig();
        config.set(MAX_OWNED_COUNT_PREFIX, null);

        for(Map.Entry<UUID, Integer> entry : maxOwnedCountMap.entrySet()) {
            UUID uuid = entry.getKey();
            int count = entry.getValue();
            String path = MAX_OWNED_COUNT_PREFIX + "." + uuid.toString();
            config.set(path, count);
        }
    }

    private void saveRealEstate() {
        File file = new File(plugin.getDataFolder(), CONFIG_FILE_NAME);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set(REAL_ESTATE_PREFIX, null);
        if(realEstateMap.isEmpty()) return;

        for(Map.Entry<String, RealEstate> entry : realEstateMap.entrySet()) {
            String name = entry.getKey();
            RealEstate realEstate = entry.getValue();
            String path = REAL_ESTATE_PREFIX + "." + name;

            config.set(path + ".world_name", realEstate.getWorldName());
            config.set(path + ".price", realEstate.getPrice());
            config.set(path + ".owner_uuid", realEstate.getOwnerUuid() == null ? null : realEstate.getOwnerUuid().toString());
            config.set(path + ".owner_name", realEstate.getOwnerName());
            config.set(path + ".template_name", realEstate.getTemplateName());
            config.set(path + ".max_chest_count", realEstate.getMaxChestCount());
            config.set(path + ".max_furnace_count", realEstate.getMaxFurnaceCount());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().info("부동산 정보 저장 실패");
        }
    }
}
