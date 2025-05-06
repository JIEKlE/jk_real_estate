package jiekie.model;

import com.sk89q.worldedit.math.BlockVector3;

import java.util.UUID;

public class RealEstate {
    private String name;
    private String worldName;
    private int price;
    private UUID ownerUuid;
    private String ownerName;
    private String templateName;
    private int maxChestCount;
    private int maxFurnaceCount;

    public RealEstate(String name, String worldName, int price) {
        this.name = name;
        this.worldName = worldName;
        this.price = price;

        this.maxChestCount = 10;
        this.maxFurnaceCount = 10;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public void setOwnerUuid(UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public int getMaxChestCount() {
        return maxChestCount;
    }

    public void setMaxChestCount(int maxChestCount) {
        this.maxChestCount = maxChestCount;
    }

    public int getMaxFurnaceCount() {
        return maxFurnaceCount;
    }

    public void setMaxFurnaceCount(int maxFurnaceCount) {
        this.maxFurnaceCount = maxFurnaceCount;
    }
}
