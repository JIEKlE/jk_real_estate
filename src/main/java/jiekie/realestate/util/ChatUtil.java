package jiekie.realestate.util;

import jiekie.realestate.model.RealEstate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatUtil {
    /* error */
    public static final String NO_ITEM = getXPrefix() + "손에 아이템을 들고 설정해주시기 바랍니다.";
    public static final String INVENTORY_FULL = getXPrefix() + "인벤토리가 가득 찼습니다. 인벤토리를 1칸 이상 비워주시기 바랍니다.";
    public static final String REGION_NOT_FOUND = getXPrefix() + "구역에 대한 정보를 찾을 수 없습니다.";
    public static final String NOT_WORLD_GUARD_REGION = getXPrefix() + "월드가드로 지정되지 않은 구역입니다.";
    public static final String DUPLICATED_REGION_NAME = getXPrefix() + "구역명이 중복됩니다. (다른 월드에 중복된 구역명 존재)";
    public static final String PLAYER_DOES_NOT_EXIST = getXPrefix() + "해당 이름을 가진 플레이어가 없습니다.";
    public static final String TEMPLATE_NOT_REGISTERED = getXPrefix() + "템플릿에 대한 정보를 찾을 수 없습니다.";
    public static final String CONTRACT_NOT_REGISTERED = getXPrefix() + "계약서가 등록되지 않았습니다.";
    public static final String NO_OWNER = getXPrefix() + "소유주가 없는 구역입니다.";
    public static final String PLAYER_CAN_NOT_OWN_MORE_REAL_ESTATE = getXPrefix() + "더 이상 부동산을 소유할 수 없습니다.";
    public static String NOT_ENOUGH_MONEY = getXPrefix() + "소지금이 부족합니다.";
    public static String SOLD = getXPrefix() + "이미 판매 완료 된 땅입니다.";
    public static String NOT_OWNER = getXPrefix() + "당신은 해당 부동산의 소유주가 아닙니다.";
    public static String CAN_NOT_BUY = getXPrefix() + "구매할 수 없는 땅입니다.";
    public static String CAN_NOT_PLACE_CHEST = getXPrefix() + "더 이상 상자를 설치할 수 없습니다.";
    public static String CAN_NOT_PLACE_FURNACE = getXPrefix() + "더 이상 화로를 설치할 수 없습니다.";

    public static String MONEY_NOT_NUMBER = getXPrefix() + "금액은 숫자만 입력할 수 있습니다.";
    public static String MAX_OWNED_COUNT_NOT_NUMBER = getXPrefix() + "개수는 숫자만 입력할 수 있습니다.";
    public static String MINUS_MONEY = getXPrefix() + "금액은 0 이상만 입력 가능합니다.";
    public static String MINUS_MAX_OWNED_COUNT = getXPrefix() + "개수는 0 이상만 입력 가능합니다.";

    /* feedback */
    public static final String REGION_IS_SAVED = getCheckPrefix() + "구역 정보를 등록했습니다.";
    public static final String REGION_IS_CHANGED = getCheckPrefix() + "구역 정보를 수정했습니다.";
    public static final String REGION_IS_REMOVED = getCheckPrefix() + "구역 정보를 제거했습니다.";
    public static final String SET_MAX_OWNED_COUNT = getCheckPrefix() + "플레이어의 최대 부동산 소유 개수를 설정했습니다.";
    public static final String SET_MAX_CHEST_COUNT = getCheckPrefix() + "구역의 최대 상자 개수를 설정했습니다.";
    public static final String SET_MAX_FURNACE_COUNT = getCheckPrefix() + "구역의 최대 화로 개수를 설정했습니다.";
    public static final String REGISTER_TEMPLATE = getCheckPrefix() + "계약서 템플릿을 등록했습니다.";
    public static final String REMOVE_TEMPLATE = getCheckPrefix() + "계약서 템플릿을 제거했습니다.";
    public static final String GET_CONTRACT = getCheckPrefix() + "계약서를 지급받았습니다.";

    /* emoji */
    public static final String HOME_EMOJI = "\uA008";
    public static final String FOR_SALE_EMOJI = "\uA009";
    public static final String SOLD_EMOJI = "\uA010";

    /* prefix */
    public static String getCheckPrefix() {
        return "\uA001 ";
    }

    public static String getXPrefix() {
        return "\uA002 ";
    }

    public static String getWarnPrefix() {
        return "\uA003 ";
    }

    public static void showMessage(CommandSender sender, String message) {
        sender.sendMessage(message);
    }

    /* validate */
    public static void notPlayer(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "플레이어가 아닙니다.");
    }

    public static void notOp(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "권한이 없습니다.");
    }

    public static String wrongCommand() {
        return getWarnPrefix() + "명령어 사용법이 잘못되었습니다.";
    }

    /* feedback */
    public static void setRealEstateContract(CommandSender sender, String name) {
        sender.sendMessage(getCheckPrefix() + name + " 부동산 계약서를 등록했습니다.");
    }

    public static void resetRealEstateContract(CommandSender sender, String name) {
        sender.sendMessage(getCheckPrefix() + name + " 부동산 계약서를 해제했습니다.");
    }

    public static void setRegionOwner(CommandSender sender, String regionName, String ownerName) {
        sender.sendMessage(getCheckPrefix() + regionName + "의 소유주를 " + ownerName + "(으)로 설정했습니다.");
    }

    public static void resetRegionOwner(CommandSender sender, String regionName) {
        sender.sendMessage(getCheckPrefix() + regionName + "의 소유권을 회수했습니다.");
    }

    public static void regionIsGiven(CommandSender sender, String regionName) {
        sender.sendMessage(getWarnPrefix() + regionName + "의 소유주가 되었습니다.");
    }

    public static void regionIsTaken(CommandSender sender, String regionName) {
        sender.sendMessage(getWarnPrefix() + regionName + "의 소유권을 회수당했습니다.");
    }

    public static void buyRegion(CommandSender sender, String regionName, String formattedPrice) {
        sender.sendMessage(getCheckPrefix() + "부동산 [" + regionName + "]을(를) 구매했습니다. " + ChatColor.RED + "( -" + formattedPrice + " )");
        sender.sendMessage(getWarnPrefix() + "중개사무소에서 부동산 계약서를 받을 수 있습니다.");
    }

    public static void sellRegion(CommandSender sender, String regionName) {
        sender.sendMessage(getCheckPrefix() + "부동산 [" + regionName + "]을(를) 판매했습니다.");
    }

    /* info */
    public static void realEstateInfoPrefix(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage("────────── 부동산 정보 ──────────");
        sender.sendMessage("");
    }

    public static void realEstateInfo(CommandSender sender, RealEstate realEstate) {
        sender.sendMessage("　구역명 : " + ChatColor.GREEN + ChatColor.BOLD + realEstate.getName());
        sender.sendMessage("　계약금 : " + NumberUtil.getFormattedMoney(realEstate.getPrice()));

        String templateName = realEstate.getTemplateName();
        if(templateName != null && !templateName.isBlank())
            sender.sendMessage("　계약서 템플릿 : " + templateName);
        
        sender.sendMessage("　최대 상자 개수 : " + realEstate.getMaxChestCount() + "개");
        sender.sendMessage("　최대 화로 개수 : " + realEstate.getMaxFurnaceCount() + "개");
        
        String ownerName = realEstate.getOwnerName();
        if(ownerName != null && !ownerName.isBlank())
            sender.sendMessage("　소유주 : " + ChatColor.YELLOW + ownerName);
        else
            sender.sendMessage("　소유주 : 없음");
    }

    public static void myRealEstateInfoPrefix(CommandSender sender, String name, int count) {
        sender.sendMessage("");
        sender.sendMessage("───────── 부동산 소유목록 ─────────");
        sender.sendMessage("");
        sender.sendMessage("　　　\uA003 소유주 : "  + ChatColor.GREEN + ChatColor.BOLD + name);
        sender.sendMessage("　　　\uA003 최대 부동산 소유 개수 : " + count + "개");
    }

    public static void myRealEstateInfo(CommandSender sender, RealEstate realEstate) {
        sender.sendMessage("　　　" + HOME_EMOJI + " " + realEstate.getName() + " (" + NumberUtil.getFormattedMoney(realEstate.getPrice()) + ")");
    }

    public static void horizontalLineSuffix(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage("──────────────────────────");
        sender.sendMessage("");
    }

    /* command */
    public static void realEstateCommandHelper(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "/부동산 도움말" + ChatColor.GRAY + " : 사용 가능한 명령어를 확인할 수 있습니다.");
    }

    public static void realEstateCommandList(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage(getWarnPrefix() + "부동산 명령어 목록");
        sender.sendMessage("　　　① /부동산 열기");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 부동산 목록을 확인하는 인벤토리를 오픈합니다.");
        if(sender.isOp()) {
            sender.sendMessage("　　　② /부동산 구역설정 구역명 금액");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 월드가드 구역을 부동산 매물로 등록합니다. (0원으로 설정 시 구매 금지)");
            sender.sendMessage("　　　③ /부동산 구역제거 구역명");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 월드가드 구역을 부동산 목록에서 제거합니다.");
            sender.sendMessage("　　　④ /부동산 소유권지정 구역명 플레이어ID|닉네임");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 입력한 구역의 소유자를 플레이어로 지정합니다.");
            sender.sendMessage("　　　⑤ /부동산 소유권회수 구역명");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 플레이어에게서 구역의 소유권을 회수합니다.");
            sender.sendMessage("　　　⑥ /부동산 소유목록");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 본인의 부동산 소유 목록을 확인합니다.");
            sender.sendMessage("　　　⑦ /부동산 소유목록 플레이어ID|닉네임");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 플레이어의 부동산 소유 목록을 확인합니다.");
            sender.sendMessage("　　　⑧ /부동산 소유개수설정 개수 플레이어ID|닉네임");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 특정 플레이어가 소유할 수 있는 부동산의 개수를 설정합니다.");
            sender.sendMessage("　　　⑨ /부동산 상자개수설정 구역명 개수");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 구역 내에 설치할 수 있는 최대 상자 개수를 설정합니다.");
            sender.sendMessage("　　　⑩ /부동산 화로개수설정 구역명 개수");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 구역 내에 설치할 수 있는 최대 화로 개수를 설정합니다.");
            sender.sendMessage("　　　⑪ /부동산 정보 구역명");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 부동산 정보를 조회합니다.");
            sender.sendMessage("　　　⑫ /부동산 도움말");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 사용 가능한 명령어를 확인할 수 있습니다.");

        } else {
            sender.sendMessage("　　　② /부동산 소유목록");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 본인의 부동산 소유 목록을 확인합니다.");
            sender.sendMessage("　　　③ /부동산 도움말");
            sender.sendMessage(ChatColor.GRAY + "　　　　　: 사용 가능한 명령어를 확인할 수 있습니다.");
        }

        sender.sendMessage("");
    }

    public static void contractCommandHelper(CommandSender sender) {
        sender.sendMessage(getWarnPrefix() + "/계약서 도움말" + ChatColor.GRAY + " : 사용 가능한 명령어를 확인할 수 있습니다.");
    }

    public static void contractCommandList(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage(getWarnPrefix() + "계약서 명령어 목록");
        sender.sendMessage("　　　① /계약서 템플릿등록 템플릿명");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 손에 든 아이템을 계약서 템플릿으로 등록합니다.");
        sender.sendMessage("　　　② /계약서 템플릿제거 템플릿명");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 계약서 템플릿을 제거합니다.");
        sender.sendMessage("　　　③ /계약서 등록 구역명 템플릿명");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 입력한 구역의 계약서를 등록합니다.");
        sender.sendMessage("　　　④ /계약서 해제 구역명");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 입력한 구역의 계약서를 해제합니다.");
        sender.sendMessage("　　　⑤ /계약서 받기 구역명");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 입력한 구역의 계약서를 받습니다.");
        sender.sendMessage("　　　⑥ /계약서 도움말");
        sender.sendMessage(ChatColor.GRAY + "　　　　　: 사용 가능한 명령어를 확인할 수 있습니다.");
        sender.sendMessage("");
    }
}
