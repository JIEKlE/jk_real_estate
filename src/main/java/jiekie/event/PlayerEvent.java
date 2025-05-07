package jiekie.event;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import jiekie.RealEstatePlugin;
import jiekie.exception.RealEstateException;
import jiekie.manager.RealEstateManager;
import jiekie.model.RealEstate;
import jiekie.util.ChatUtil;
import jiekie.util.ItemUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerEvent implements Listener {
    private final RealEstatePlugin plugin;
    private final Map<UUID, Set<String>> playerRegionMap = new HashMap<>();

    public PlayerEvent(RealEstatePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        setMaxOwnedCount(e.getPlayer());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        onChestPlace(e);
        onFurnacePlace(e);
    }

    private void onChestPlace(BlockPlaceEvent e) {
        Block block = e.getBlock();
        Material type = block.getType();
        if(type != Material.CHEST && type != Material.BARREL && type != Material.TRAPPED_CHEST) return;

        Player player = e.getPlayer();
        World world = player.getWorld();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(world));
        if(regionManager == null) return;

        ApplicableRegionSet regionSet = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(block.getLocation()));
        RealEstateManager realEstateManager = plugin.getRealEstateManager();
        for(ProtectedRegion region : regionSet) {
            try {
                String regionName = region.getId();
                if(!realEstateManager.isRegionExist(regionName)) continue;

                RealEstate realEstate = realEstateManager.getRealEstateOrThrow(regionName);
                int maxCount = realEstate.getMaxChestCount();
                int count = countBlocksInRegion(region, world, Material.CHEST)
                        + countBlocksInRegion(region, world, Material.BARREL)
                        + countBlocksInRegion(region, world, Material.TRAPPED_CHEST);
                if(count > maxCount) {
                    e.setCancelled(true);
                    ChatUtil.showMessage(player, ChatUtil.CAN_NOT_PLACE_CHEST);
                }

            } catch (RealEstateException ex) {
                Bukkit.getLogger().info(ex.getMessage());
            }
        }
    }

    private void onFurnacePlace(BlockPlaceEvent e) {
        Block block = e.getBlock();
        Material type = block.getType();
        if(type != Material.FURNACE && type != Material.BLAST_FURNACE && type != Material.SMOKER) return;

        Player player = e.getPlayer();
        World world = player.getWorld();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(world));
        if(regionManager == null) return;

        ApplicableRegionSet regionSet = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(block.getLocation()));
        RealEstateManager realEstateManager = plugin.getRealEstateManager();
        for(ProtectedRegion region : regionSet) {
            try {
                String regionName = region.getId();
                if(!realEstateManager.isRegionExist(regionName)) continue;

                RealEstate realEstate = realEstateManager.getRealEstateOrThrow(regionName);
                int maxCount = realEstate.getMaxFurnaceCount();
                int count = countBlocksInRegion(region, world, Material.FURNACE)
                            + countBlocksInRegion(region, world, Material.BLAST_FURNACE)
                            + countBlocksInRegion(region, world, Material.SMOKER);
                if(count > maxCount) {
                    e.setCancelled(true);
                    ChatUtil.showMessage(player, ChatUtil.CAN_NOT_PLACE_FURNACE);
                }

            } catch (RealEstateException ex) {
                Bukkit.getLogger().info(ex.getMessage());
            }
        }
    }

    private int countBlocksInRegion(ProtectedRegion region, World world, Material material) {
        BlockVector3 minPoint = region.getMinimumPoint();
        BlockVector3 maxPoint = region.getMaximumPoint();

        int count = 0;
        for(int x = minPoint.getBlockX(); x <= maxPoint.getBlockX(); x++) {
            for(int y = minPoint.getBlockY() ; y <= maxPoint.getBlockY(); y++) {
                for(int z = minPoint.getBlockZ() ; z <= maxPoint.getBlockZ(); z++) {
                    if(world.getBlockAt(x, y, z).getType() == material)
                        count++;
                }
            }
        }

        return count;
    }

    @EventHandler
    public void onInteract(PlayerMoveEvent e) {
        onPlayerEnterOrLeave(e);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        onContractClick(e);
    }

    private void onContractClick(PlayerInteractEvent e) {
        if(!Objects.equals(e.getHand(), EquipmentSlot.HAND)) return;
        if(e.getAction() != Action.RIGHT_CLICK_AIR  && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = e.getPlayer();
        PlayerInventory inventory = player.getInventory();
        ItemStack item = inventory.getItemInMainHand();

        try {
            if(!ItemUtil.isContractName(item)) return;
            String regionName = ItemUtil.getRegionNameFromContract(item);

            RealEstate realEstate = plugin.getRealEstateManager().getRealEstateOrThrow(regionName);
            ItemStack contract = plugin.getContractManager().getContract(realEstate);
            if(!ItemUtil.compareContractWithTemplate(item, contract)) return;

            String contractType = ItemUtil.getContractType(item);
            showRealEstateTransactionPrompt(player, contractType, regionName);

        } catch (RealEstateException ex) {
            Bukkit.getLogger().info(ex.getMessage());
        }
    }

    private void showRealEstateTransactionPrompt(Player player, String contractType, String regionName) {
        TextComponent message = new TextComponent(ChatUtil.getWarnPrefix() + regionName + "을(를) " + contractType + "하시겠습니까?");

        TextComponent yes = new TextComponent(ChatColor.GREEN + "　[ " + ChatUtil.getCheckPrefix() + "예 ]");
        yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/부동산 " + contractType + " " + regionName + " " + player.getName()));

        TextComponent no = new TextComponent(ChatColor.RED + "　[ " + ChatUtil.getXPrefix() + "아니오 ]");

        message.addExtra(yes);
        message.addExtra(no);

        player.spigot().sendMessage(message);
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

    private void onPlayerEnterOrLeave(PlayerMoveEvent e) {
        if(e.getFrom().getBlock().equals(e.getTo().getBlock())) return;

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        World world = player.getWorld();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(world));
        if(regionManager == null) return;

        Set<String> newRegions = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(e.getTo()))
                .getRegions()
                .stream()
                .map(ProtectedRegion::getId)
                .collect(Collectors.toSet());
        Set<String> oldRegions = playerRegionMap.getOrDefault(uuid, Collections.emptySet());
        RealEstateManager realEstateManager = plugin.getRealEstateManager();
        String message = null;

        try {
            // enter
            for(String regionName : newRegions) {
                if(oldRegions.contains(regionName)) continue;
                if(!realEstateManager.isRegionExist(regionName)) continue;
                RealEstate realEstate = realEstateManager.getRealEstateOrThrow(regionName);
                String ownerName = realEstate.getOwnerName();
                if(ownerName != null && !ownerName.isBlank()) {
                    message = ChatUtil.HOME_EMOJI + " " + ownerName + " 소유의 땅 (" + realEstate.getName() + ")";
                } else {
                    message = ChatUtil.HOME_EMOJI + " 소유주 없음 (" + realEstate.getName() + ")";
                }
                break;
            }

            boolean wasInRealEstate = oldRegions.stream().anyMatch(realEstateManager::isRegionExist);
            boolean isNowInRealEstate = newRegions.stream().anyMatch(realEstateManager::isRegionExist);

            if(!isNowInRealEstate && wasInRealEstate)
                message = "";

            if(message != null)
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));

            playerRegionMap.put(uuid, newRegions);

        } catch (RealEstateException ex) {
            Bukkit.getLogger().info(ex.getMessage());
        }
    }
}
