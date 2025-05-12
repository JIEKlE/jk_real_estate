package jiekie.realestate.command;

import jiekie.realestate.RealEstatePlugin;
import jiekie.realestate.exception.RealEstateException;
import jiekie.realestate.manager.RealEstateManager;
import jiekie.realestate.model.RealEstate;
import jiekie.realestate.util.ChatUtil;
import jiekie.realestate.util.SoundUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class ContractCommand implements CommandExecutor {
    private final RealEstatePlugin plugin;

    public ContractCommand(RealEstatePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            ChatUtil.notPlayer(sender);
            return true;
        }

        if(!player.isOp()) {
            ChatUtil.notOp(sender);
            return true;
        }

        if(args == null || args.length == 0) {
            ChatUtil.contractCommandHelper(sender);
            return true;
        }

        switch (args[0]) {
            case "템플릿등록":
                registerTemplate(player, args);
                break;

            case "템플릿제거":
                removeTemplate(player, args);
                break;

            case "등록":
                setRealEstateContract(player, args);
                break;

            case "해제":
                resetRealEstateContract(player, args);
                break;

            case "받기":
                getContract(player, args);
                break;

            case "도움말":
                ChatUtil.contractCommandList(sender);
                break;

            default:
                ChatUtil.contractCommandHelper(sender);
                break;
        }

        return true;
    }

    private void registerTemplate(Player player, String[] args) {
        if(args.length < 2) {
            player.sendMessage(ChatUtil.wrongCommand() + " (/계약서 템플릿등록 템플릿명)");
            return;
        }

        PlayerInventory inventory = player.getInventory();
        ItemStack template = inventory.getItemInMainHand();
        if(template.getType() == Material.AIR) {
            ChatUtil.showMessage(player, ChatUtil.NO_ITEM);
            return;
        }

        plugin.getContractManager().registerTemplate(args[1], template);

        ChatUtil.showMessage(player, ChatUtil.REGISTER_TEMPLATE);
        SoundUtil.playNoteBlockBell(player);
    }

    private void removeTemplate(Player player, String[] args) {
        if(args.length < 2) {
            player.sendMessage(ChatUtil.wrongCommand() + " (/계약서 템플릿제거 템플릿명)");
            return;
        }

        String templateName = args[1];
        if(!plugin.getContractManager().existTemplate(templateName)) {
            ChatUtil.showMessage(player, ChatUtil.TEMPLATE_NOT_REGISTERED);
            return;
        }

        plugin.getContractManager().removeTemplate(templateName);

        ChatUtil.showMessage(player, ChatUtil.REMOVE_TEMPLATE);
        SoundUtil.playNoteBlockBell(player);
    }

    private void setRealEstateContract(Player player, String[] args) {
        if(args.length < 3) {
            player.sendMessage(ChatUtil.wrongCommand() + " (/계약서 등록 구역명 템플릿명)");
            return;
        }

        try {
            String regionName = args[1];
            String templateName = args[2];
            if(!plugin.getContractManager().existTemplate(templateName)) {
                ChatUtil.showMessage(player, ChatUtil.TEMPLATE_NOT_REGISTERED);
                return;
            }

            plugin.getRealEstateManager().setTemplate(regionName, templateName);

            ChatUtil.setRealEstateContract(player, regionName);
            SoundUtil.playNoteBlockBell(player);

        } catch (RealEstateException e) {
            ChatUtil.showMessage(player, e.getMessage());
        }
    }

    private void resetRealEstateContract(Player player, String[] args) {
        if(args.length < 2) {
            player.sendMessage(ChatUtil.wrongCommand() + " (/계약서 해제 구역명)");
            return;
        }

        try {
            String regionName = args[1];
            RealEstateManager realEstateManager = plugin.getRealEstateManager();
            RealEstate realEstate = realEstateManager.getRealEstateOrThrow(regionName);

            String templateName = realEstate.getTemplateName();
            if(templateName == null || templateName.isBlank()) {
                ChatUtil.showMessage(player, ChatUtil.CONTRACT_NOT_REGISTERED);
                return;
            }

            realEstateManager.setTemplate(regionName, null);

            ChatUtil.resetRealEstateContract(player, regionName);
            SoundUtil.playNoteBlockBell(player);

        } catch (RealEstateException e) {
            ChatUtil.showMessage(player, e.getMessage());
        }
    }

    private void getContract(Player player, String[] args) {
        if(args.length < 2) {
            player.sendMessage(ChatUtil.wrongCommand() + " (/계약서 받기 구역명)");
            return;
        }

        PlayerInventory inventory = player.getInventory();
        if(inventory.firstEmpty() == -1) {
            ChatUtil.showMessage(player, ChatUtil.INVENTORY_FULL);
            return;
        }

        try {
            String regionName = args[1];
            RealEstate realEstate;
            realEstate = plugin.getRealEstateManager().getRealEstateOrThrow(regionName);

            ItemStack contract = plugin.getContractManager().getContract(realEstate);
            inventory.addItem(contract);

            ChatUtil.showMessage(player, ChatUtil.GET_CONTRACT);
            SoundUtil.playNoteBlockBell(player);

        } catch (RealEstateException e) {
            ChatUtil.showMessage(player, e.getMessage());
        }
    }
}
