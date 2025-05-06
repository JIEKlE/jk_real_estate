package jiekie.completer;

import jiekie.RealEstatePlugin;
import jiekie.api.NicknameAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RealEstateTabCompleter implements TabCompleter {
    private final RealEstatePlugin plugin;

    public RealEstateTabCompleter(RealEstatePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) return Collections.emptyList();

        int length = args.length;
        if(length == 1) {
            if(player.isOp())
                return Arrays.asList("열기", "구역설정", "구역제거", "소유권지정", "소유권회수"
                        , "소유목록", "소유개수설정", "상자개수설정", "화로개수설정", "정보"
                        , "도움말");
            else
                return Arrays.asList("열기", "소유목록", "도움말");
        }

        String commandType = args[0];
        if(length == 2) {
            switch (commandType) {
                case "구역설정" -> { return plugin.getRealEstateManager().getWorldGuardRegionNames(player.getWorld()); }
                case "구역제거", "소유권지정", "소유권회수", "상자개수설정", "화로개수설정", "정보" -> { return plugin.getRealEstateManager().getRealEstateNames(); }
                case "소유목록" -> {
                    if (player.isOp())
                        return NicknameAPI.getInstance().getPlayerNameAndNicknameList();
                }
                case "소유개수설정" -> { return List.of("개수"); }
            }
        }

        if(length == 3) {
            switch (commandType) {
                case "구역설정" -> { return List.of("금액"); }
                case "소유권지정", "소유개수설정" -> { return NicknameAPI.getInstance().getPlayerNameAndNicknameList(); }
                case "상자개수설정", "화로개수설정" -> { return List.of("개수"); }
            }
        }

        return Collections.emptyList();
    }
}
