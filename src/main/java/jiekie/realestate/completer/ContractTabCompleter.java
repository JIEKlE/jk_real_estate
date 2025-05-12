package jiekie.realestate.completer;

import jiekie.realestate.RealEstatePlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ContractTabCompleter implements TabCompleter {
    private final RealEstatePlugin plugin;

    public ContractTabCompleter(RealEstatePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.hasPermission("jk.contract.command")) return Collections.emptyList();
        if(!(sender instanceof Player)) return Collections.emptyList();

        int length = args.length;
        if(length == 1) {
            return Arrays.asList("템플릿등록", "템플릿제거", "등록", "해제", "받기", "도움말");
        }

        String commandType = args[0];
        if(length == 2) {
            switch (commandType) {
                case "템플릿등록" -> { return List.of("템플릿명"); }
                case "템플릿제거" -> { return plugin.getContractManager().getTemplateNames(); }
                case "등록", "해제", "받기" -> { return plugin.getRealEstateManager().getRealEstateNames(); }
            }
        }

        if(length == 3) {
            if(commandType.equals("등록"))
                return plugin.getContractManager().getTemplateNames();
        }

        return Collections.emptyList();
    }
}
