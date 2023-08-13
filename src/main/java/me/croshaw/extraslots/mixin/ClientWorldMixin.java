package me.croshaw.extraslots.mixin;

import me.croshaw.extraslots.ExtraSlots;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Inject(method = "addPlayer", at = @At("TAIL"))
    private void addPlayer(int id, AbstractClientPlayerEntity player, CallbackInfo ci) {
        ClientPlayNetworking.send(ExtraSlots.REQUEST_SYNC_MESSAGE_C2S, PacketByteBufs.create());
    }
}
