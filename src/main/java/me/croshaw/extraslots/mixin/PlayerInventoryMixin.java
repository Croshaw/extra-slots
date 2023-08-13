package me.croshaw.extraslots.mixin;

import me.croshaw.extraslots.player.inventory.AdditionalInventory;
import me.croshaw.extraslots.utils.InventoryHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Shadow
    protected abstract boolean canStackAddMore(ItemStack existingStack, ItemStack stack);

    @Inject(at=@At("TAIL"), method = "getEmptySlot", cancellable = true)
    private void getEmptySlot(CallbackInfoReturnable<Integer> cir) {
        if(cir.getReturnValue() == -1) {
            PlayerEntity player = ((PlayerInventory)(Object)this).player;
            AdditionalInventory addInventory = InventoryHelper.getInventory(player);
            for(int i = addInventory.inventory.size()-1; i >= 0; i--) {
                if (addInventory.inventory.get(i).isEmpty()) {
                    cir.setReturnValue(i+41);
                }
            }
        }
    }

    @Inject(at=@At("TAIL"), method = "updateItems")
    private void updateItems(CallbackInfo ci) {
        PlayerEntity player = ((PlayerInventory)(Object)this).player;
        AdditionalInventory addInventory = InventoryHelper.getInventory(player);
        for (int i = 0; i < addInventory.inventory.size(); ++i) {
            if (!addInventory.inventory.get(i).isEmpty()) {
                addInventory.inventory.get(i).inventoryTick(player.world, player, i, false);
            }
        }
    }

    @Inject(at=@At("TAIL"), method = "dropAll")
    private void dropAll(CallbackInfo ci) {
        PlayerEntity player = ((PlayerInventory)(Object)this).player;
        AdditionalInventory addInventory = InventoryHelper.getInventory(player);
        for(int i = 0; i<addInventory.inventory.size();i++) {
            player.dropItem(addInventory.inventory.get(i), true, false);
            addInventory.inventory.set(i, ItemStack.EMPTY);
        }
    }

    @Inject(at=@At("HEAD"), method = "getStack", cancellable = true)
    private void getStack(int slot, CallbackInfoReturnable<ItemStack> cir) {
        if(slot >= 41) {
            PlayerEntity player = ((PlayerInventory)(Object)this).player;
            AdditionalInventory addInventory = InventoryHelper.getInventory(player);
            cir.setReturnValue(addInventory.inventory.get(slot-41));
        }
    }

    @Inject(at=@At("TAIL"), method = "getOccupiedSlotWithRoomForStack", cancellable = true)
    private void getOccupiedSlotWithRoomForStack(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if(cir.getReturnValue() == -1) {
            PlayerEntity player = ((PlayerInventory)(Object)this).player;
            AdditionalInventory addInventory = InventoryHelper.getInventory(player);
            for(int i = addInventory.inventory.size()-1; i >= 0; i--) {
                if (this.canStackAddMore(addInventory.inventory.get(i), stack)) {
                    cir.setReturnValue(i+41);
                }
            }
        }
    }

    @Inject(at=@At(value = "HEAD"), method = "setStack", cancellable = true)
    private void setStack(int slot, ItemStack stack, CallbackInfo ci) {
        if(slot>=41) {
            PlayerEntity player = ((PlayerInventory)(Object)this).player;
            AdditionalInventory addInventory = InventoryHelper.getInventory(player);
            addInventory.inventory.set(slot-41, stack);
            ci.cancel();
        }
    }

    @Inject(at=@At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/player/PlayerInventory;getEmptySlot()I"), method = "insertStack(ILnet/minecraft/item/ItemStack;)Z", cancellable = true)
    private void insertStack(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if(slot>=41) {
            PlayerEntity player = ((PlayerInventory)(Object)this).player;
            AdditionalInventory addInventory = InventoryHelper.getInventory(player);
            addInventory.inventory.set(slot-41, stack.copy());
            addInventory.inventory.get(slot-41).setBobbingAnimationTime(5);
            stack.setCount(0);
            cir.setReturnValue(true);
        }
    }
}