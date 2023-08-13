package me.croshaw.extraslots.player.inventory;

import me.croshaw.extraslots.utils.InventoryHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdditionalInventory {
    private final PlayerEntity player;
    public final ArrayList<ItemStack> inventory;
    public AdditionalInventory(PlayerEntity player, int size) {
        this.player = player;
        inventory = new ArrayList<>(Collections.nCopies(size, ItemStack.EMPTY));
    }

    public void clone(AdditionalInventory otherAddInventory) {
        inventory.clear();
        otherAddInventory.inventory.forEach(x -> inventory.add(x.copy()));
        //inventory.addAll(otherAddInventory.inventory);//мб нужен copy
    }

    public void resize(int size) {
        if(inventory.size()==size) return;
        if(inventory.size() > size) {
            for (int i = inventory.size()-1;i >= size; i--) {
                player.dropStack(inventory.get(i));
                inventory.remove(i);
            }
        } else {
            inventory.addAll(Collections.nCopies(size-inventory.size(), ItemStack.EMPTY));
        }

    }

    public void readNbt(NbtList nbtList) {
        inventory.clear();
        int size = nbtList.getCompound(0).getInt("Inv Size");
        inventory.addAll(Collections.nCopies(size, ItemStack.EMPTY));
        for(int i = 1; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("Slot") & 255;
            ItemStack itemStack = ItemStack.fromNbt(nbtCompound);
            if (!itemStack.isEmpty()) {
                this.inventory.set(j, itemStack);
            }
        }
    }

    public NbtList writeNbt(NbtList nbtList) {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putInt("Inv Size", inventory.size());
        nbtList.add(nbtCompound);
        for(int i = 0; i < this.inventory.size(); i++) {
            if (!this.inventory.get(i).isEmpty()) {
                nbtCompound = new NbtCompound();
                nbtCompound.putByte("Slot", (byte)i);
                this.inventory.get(i).writeNbt(nbtCompound);
                nbtList.add(nbtCompound);
            }
        }
        return nbtList;
    }

    public void restoreInventories() {
        int amount = InventoryHelper.getCurrentScrollPos(player);
        replaceInventories(amount);
    }

    public void replaceInventories(double amount) {
        List<ItemStack> playerInventory = player.getInventory().main;
        List<ItemStack> cachedInventory = new ArrayList<>();
        for (int i = 9; i < 36; i++)
            cachedInventory.add(playerInventory.get(i).copy());
        inventory.forEach(itemStack -> cachedInventory.add(itemStack.copy()));
        shiftArray(cachedInventory, (int) (9 * amount));
        for (int i = 9; i < 36; i++)
            playerInventory.set(i, cachedInventory.get(i - 9).copy());
        for (int i = 0; i < inventory.size(); i++)
            inventory.set(i, cachedInventory.get(i + 27).copy());
        cachedInventory.clear();
    }

    public static void shiftArray(List<ItemStack> array, int position) {
        int size = array.size();
        if (position > 0) {
            position %= size;
            Collections.rotate(array.subList(0, size), position);
        } else if (position < 0) {
            position = -position % size;
            Collections.rotate(array.subList(0, size), size - position);
        }
    }
}
