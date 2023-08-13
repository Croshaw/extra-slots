package me.croshaw.extraslots.player;

import me.croshaw.extraslots.player.inventory.AdditionalInventory;
import net.minecraft.nbt.NbtCompound;

public interface IPlayerExtendedData {
    AdditionalInventory getAdditionInventory();
    void setAdditionInventory(AdditionalInventory inventory);
    void cloneInventory(AdditionalInventory otherInventory, int lastScrollPos);
    int getLastScrollPos();
    void setLastScrollPos(int pos);
    void readExtendedNbtData(NbtCompound nbt);
    void writeExtendedNbtData(NbtCompound nbt);
}
