package jiekie.realestate.command;

import jiekie.nickname.api.NicknameAPI;
import jiekie.nickname.model.PlayerNameData;
import jiekie.realestate.RealEstatePlugin;
import jiekie.realestate.exception.RealEstateException;
import jiekie.realestate.manager.RealEstateManager;
import jiekie.realestate.model.CommandContext;
import jiekie.realestate.model.RealEstate;
import jiekie.realestate.util.ChatUtil;
import jiekie.realestate.util.SoundUtil;
import jiekie.realestate.util.StringUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class RealEstateCommand implements CommandExecutor {
    private final RealEstatePlugin plugin;
    private final Map<String, Consumer<CommandContext>> commandMap = new HashMap<>();

    public RealEstateCommand(RealEstatePlugin plugin) {
        this.plugin = plugin;
        registerCommands();
    }

    private void registerCommands() {
        commandMap.put("열기", this::open);
        commandMap.put("구역설정", this::setRegion);
        commandMap.put("구역제거", this::removeRegion);
        commandMap.put("소유권지정", this::giveRegion);
        commandMap.put("소유권회수", this::takeRegion);
        commandMap.put("소유목록", this::showOwnRegions);
        commandMap.put("소유개수설정", this::setMaxOwnedCount);
        commandMap.put("상자개수설정", this::setMaxChestCount);
        commandMap.put("화로개수설정", this::setMaxFurnaceCount);
        commandMap.put("정보", this::showRegionInfo);
        commandMap.put("도움말", ctx -> ChatUtil.realEstateCommandList(ctx.sender()));

        // console only
        commandMap.put("구매", this::buyRealEstate);
        commandMap.put("판매", this::sellRealEstate);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args == null || args.length == 0) {
            ChatUtil.realEstateCommandHelper(sender);
            return true;
        }

        Consumer<CommandContext> executer = commandMap.get(args[0]);
        if(executer == null) {
            ChatUtil.realEstateCommandHelper(sender);
            return true;
        }

        executer.accept(new CommandContext(sender, args));
        return true;
    }

    private void open(CommandContext context) {
        CommandSender sender = context.sender();

        Player player = asPlayer(sender);
        if(player == null) return;

        plugin.getRealEstateManager().open(player, 1);
    }

    private void setRegion(CommandContext context) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        if(args.length < 3) {
            sender.sendMessage(ChatUtil.wrongCommand() + " (/부동산 구역설정 구역명 금액)");
            return;
        }

        Player player = asPlayer(sender);
        if(player == null) return;
        if(!player.isOp()) {
            ChatUtil.notOp(sender);
            return;
        }

        try {
            String regionName = args[1];
            RealEstateManager realEstateManager = plugin.getRealEstateManager();
            boolean regionExist = realEstateManager.isRegionExist(regionName);
            realEstateManager.setRegion(player.getWorld(), regionName, args[2]);

            ChatUtil.showMessage(sender, regionExist ? ChatUtil.REGION_IS_CHANGED : ChatUtil.REGION_IS_SAVED);
            SoundUtil.playNoteBlockBell(player);

        } catch (RealEstateException e) {
            ChatUtil.showMessage(sender, e.getMessage());
        }
    }

    private void removeRegion(CommandContext context) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        if(args.length < 2) {
            sender.sendMessage(ChatUtil.wrongCommand() + " (/부동산 구역제거 구역명)");
            return;
        }

        Player player = asPlayer(sender);
        if(player == null) return;
        if(!player.isOp()) {
            ChatUtil.notOp(sender);
            return;
        }

        try {
            plugin.getRealEstateManager().removeRegion(args[1]);

            ChatUtil.showMessage(sender, ChatUtil.REGION_IS_REMOVED);
            SoundUtil.playNoteBlockBell(player);

        } catch (RealEstateException e) {
            ChatUtil.showMessage(sender, e.getMessage());
        }
    }

    private void giveRegion(CommandContext context) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        if(args.length < 3) {
            sender.sendMessage(ChatUtil.wrongCommand() + " (/부동산 소유권지정 구역명 플레이어ID|닉네임)");
            return;
        }

        if(!sender.isOp()) {
            ChatUtil.notOp(sender);
            return;
        }

        try {
            String regionName = args[1];
            String playerName = StringUtil.getContents(args, 2);
            Player targetPlayer = NicknameAPI.getInstance().getPlayerByNameOrNickname(playerName);
            if(targetPlayer == null) {
                ChatUtil.showMessage(sender, ChatUtil.PLAYER_DOES_NOT_EXIST);
                return;
            }

            plugin.getRealEstateManager().giveRegion(regionName, targetPlayer, playerName);

            if(sender instanceof Player player) {
                ChatUtil.setRegionOwner(sender, regionName, playerName);
                SoundUtil.playNoteBlockBell(player);
            }

        } catch (RealEstateException e) {
            ChatUtil.showMessage(sender, e.getMessage());
        }
    }

    private void takeRegion(CommandContext context) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        if(args.length < 2) {
            sender.sendMessage(ChatUtil.wrongCommand() + " (/부동산 소유권회수 구역명)");
            return;
        }

        if(!sender.isOp()) {
            ChatUtil.notOp(sender);
            return;
        }

        try {
            String regionName = args[1];
            plugin.getRealEstateManager().takeRegion(regionName);

            if(sender instanceof Player player) {
                ChatUtil.resetRegionOwner(sender, regionName);
                SoundUtil.playNoteBlockBell(player);
            }

        } catch (RealEstateException e) {
            ChatUtil.showMessage(sender, e.getMessage());
        }
    }

    private void showOwnRegions(CommandContext context) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        // 본인
        String playerName;
        Player player;
        if(args.length == 1) {
            player = asPlayer(sender);
            if(player == null) return;

            PlayerNameData playerNameData = NicknameAPI.getInstance().getPlayerNameData(player.getUniqueId());
            playerName = playerNameData == null ? player.getName() : playerNameData.getNickname();

        // 타인
        } else {
            if(!sender.isOp()) {
                ChatUtil.notOp(sender);
                return;
            }

            playerName = StringUtil.getContents(args, 1);
            player = NicknameAPI.getInstance().getPlayerByNameOrNickname(playerName);
            if(player == null) {
                ChatUtil.showMessage(sender, ChatUtil.PLAYER_DOES_NOT_EXIST);
                return;
            }
        }

        plugin.getRealEstateManager().showOwnRegions(sender, player, playerName);
    }

    private void setMaxOwnedCount(CommandContext context) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        if(args.length < 3) {
            sender.sendMessage(ChatUtil.wrongCommand() + " (/부동산 소유개수설정 개수 플레이어ID|닉네임)");
            return;
        }

        if(!sender.isOp()) {
            ChatUtil.notOp(sender);
            return;
        }

        try {
            String playerName = StringUtil.getContents(args, 2);
            plugin.getRealEstateManager().setMaxOwnedCount(playerName, args[1]);

            if(sender instanceof Player player) {
                ChatUtil.showMessage(sender, ChatUtil.SET_MAX_OWNED_COUNT);
                SoundUtil.playNoteBlockBell(player);
            }

        } catch (RealEstateException e) {
            ChatUtil.showMessage(sender, e.getMessage());
        }
    }

    private void setMaxChestCount(CommandContext context) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        if(args.length < 3) {
            sender.sendMessage(ChatUtil.wrongCommand() + " (/부동산 상자개수설정 구역명 개수");
            return;
        }

        Player player = asPlayer(sender);
        if(player == null) return;
        if(!player.isOp()) {
            ChatUtil.notOp(sender);
            return;
        }

        try {
            plugin.getRealEstateManager().setMaxChestCount(args[1], args[2]);
            ChatUtil.showMessage(sender, ChatUtil.SET_MAX_CHEST_COUNT);
            SoundUtil.playNoteBlockBell(player);

        } catch (RealEstateException e) {
            ChatUtil.showMessage(player, e.getMessage());
        }
    }

    private void setMaxFurnaceCount(CommandContext context) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        if(args.length < 3) {
            sender.sendMessage(ChatUtil.wrongCommand() + " (/부동산 화로개수설정 구역명 개수");
            return;
        }

        Player player = asPlayer(sender);
        if(player == null) return;
        if(!player.isOp()) {
            ChatUtil.notOp(sender);
            return;
        }

        try {
            plugin.getRealEstateManager().setMaxFurnaceCount(args[1], args[2]);
            ChatUtil.showMessage(sender, ChatUtil.SET_MAX_FURNACE_COUNT);
            SoundUtil.playNoteBlockBell(player);

        } catch (RealEstateException e) {
            ChatUtil.showMessage(player, e.getMessage());
        }
    }

    private void showRegionInfo(CommandContext context) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        if(args.length < 2) {
            sender.sendMessage(ChatUtil.wrongCommand() + " (/부동산 정보 구역명)");
            return;
        }

        if(!sender.isOp()) {
            ChatUtil.notOp(sender);
            return;
        }

        try {
            RealEstateManager realEstateManager = plugin.getRealEstateManager();
            RealEstate realEstate = realEstateManager.getRealEstateOrThrow(args[1]);

            ChatUtil.realEstateInfoPrefix(sender);
            ChatUtil.realEstateInfo(sender, realEstate);
            ChatUtil.horizontalLineSuffix(sender);

            if(sender instanceof Player player)
                SoundUtil.playNoteBlockBell(player);

        } catch (RealEstateException e) {
            ChatUtil.showMessage(sender, e.getMessage());
        }
    }

    private void buyRealEstate(CommandContext context) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        try {
            plugin.getRealEstateManager().buyRegion(args[1], args[2]);
        } catch (RealEstateException e) {
            ChatUtil.showMessage(sender, e.getMessage());
        }
    }

    private void sellRealEstate(CommandContext context) {
        CommandSender sender = context.sender();
        String[] args = context.args();

        try {
            plugin.getRealEstateManager().sellRegion(args[1], args[2]);
        } catch (RealEstateException e) {
            ChatUtil.showMessage(sender, e.getMessage());
        }
    }

    private Player asPlayer(CommandSender sender) {
        if(!(sender instanceof Player)) {
            ChatUtil.notPlayer(sender);
            return null;
        }

        return (Player) sender;
    }
}
