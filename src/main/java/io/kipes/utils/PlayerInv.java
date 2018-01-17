package io.kipes.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerInv {

    private ItemStack[] contents;
    private ItemStack[] armorContents;

    public PlayerInv() {
    }

    public PlayerInv(final ItemStack[] contents, final ItemStack[] armorContents) {
        this.contents = contents;
        this.armorContents = armorContents;
    }

    public static PlayerInv fromPlayerInventory(PlayerInventory inv) {
        return new PlayerInv(inv.getContents(), inv.getArmorContents());
    }

    public ItemStack[] getContents() {
        return this.contents;
    }

    public void setContents(final ItemStack[] contents) {
        this.contents = contents;
    }

    public ItemStack[] getArmorContents() {
        return this.armorContents;
    }

    public void setArmorContents(final ItemStack[] armorContents) {
        this.armorContents = armorContents;
    }

    public ItemStack getHelmet() {
        return armorContents[0];
    }

    public ItemStack getChestPiece() {
        return armorContents[1];
    }

    public ItemStack getLeggings() {
        return armorContents[2];
    }

    public ItemStack getBoots() {
        return armorContents[3];
    }

    public ItemStack getSword() {
        return contents[0];
    }

}
