package jiekie.util;

import org.bukkit.ChatColor;

public class StringUtil {
    public static String getContents(String[] args, int startIndex) {
        StringBuilder sb = new StringBuilder();

        for(int i = startIndex; i < args.length; ++i) {
            if (i != startIndex) {
                sb.append(" ");
            }

            sb.append(args[i]);
        }

        String contents = sb.toString();
        return ChatColor.translateAlternateColorCodes('&', contents);
    }
}
