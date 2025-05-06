package jiekie.util;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberUtil {
    public static String getFormattedMoney(int money) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.KOREA);
        formatter.setMaximumFractionDigits(0);
        return formatter.format(money) + "원";
    }

    public static int getUnformattedMoney(String money) {
        if(money == null || money.isBlank()) return 0;
        String unformattedMoney = money.replaceAll("원", "").replaceAll(",", "");
        try {
            return Integer.parseInt(unformattedMoney);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
