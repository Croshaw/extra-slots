package me.croshaw.extraslots.mixin;

import me.croshaw.extraslots.player.IPlayerExtendedData;
import me.croshaw.extraslots.player.inventory.AdditionalInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements IPlayerExtendedData {
    @Shadow public abstract PlayerInventory getInventory();

    @Unique
    private AdditionalInventory additionalInventory;
    @Unique
    private int lastScrollPos;
    @Override
    public AdditionalInventory getAdditionInventory() {
        if(additionalInventory == null)
            additionalInventory = new AdditionalInventory((PlayerEntity)(Object) this, 0);
        return additionalInventory;
    }

    @Override
    public void setAdditionInventory(AdditionalInventory inventory) {
        additionalInventory = inventory;
        lastScrollPos = 0;
    }

    @Override
    public void cloneInventory(AdditionalInventory otherInventory, int lastScrollPos) {
        getAdditionInventory().clone(otherInventory);
        setLastScrollPos(lastScrollPos);
    }

    @Override
    public void readExtendedNbtData(NbtCompound nbt) {
        if(nbt.contains("extraslots.inventory", 9))
            getAdditionInventory().readNbt(nbt.getList("extraslots.inventory", 10));
        if(nbt.contains("extraslots.scroll"))
            lastScrollPos = nbt.getInt("extraslots.scroll");
    }

    @Override
    public void writeExtendedNbtData(NbtCompound nbt) {
        nbt.put("extraslots.inventory", getAdditionInventory().writeNbt(new NbtList()));
        nbt.putInt("extraslots.scroll", lastScrollPos);
    }


    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        readExtendedNbtData(nbt);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        writeExtendedNbtData(nbt);
    }

    @Override
    public int getLastScrollPos() {
        return lastScrollPos;
    }

    @Override
    public void setLastScrollPos(int pos) {
        lastScrollPos=pos;
    }
}
