package jiekie.util;

import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class SoundUtil {
    public static void playNoteBlockBell(Player player) {
        player.playSound(player.getLocation(), "minecraft:block.note_block.bell", SoundCategory.MASTER, 0.5f, 1.0f);
    }
}
