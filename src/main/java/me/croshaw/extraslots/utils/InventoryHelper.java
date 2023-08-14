package me.croshaw.extraslots.utils;

import me.croshaw.extraslots.client.ExtraSlotsClient;
import me.croshaw.extraslots.player.IPlayerExtendedData;
import me.croshaw.extraslots.player.inventory.AdditionalInventory;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class InventoryHelper {
    public static IPlayerExtendedData getExtendedPlayer(PlayerEntity player) {
        return (IPlayerExtendedData) player;
    }

    public static AdditionalInventory getInventory(PlayerEntity player) {
        return getExtendedPlayer(player).getAdditionInventory();
    }

    public static void createInventory(ServerPlayerEntity player, int size) {
        if(player.isCreative()) return;
        getExtendedPlayer(player).setAdditionInventory(new AdditionalInventory(player, size));
        sendToClient(player, true);
    }

    public static int getSize(PlayerEntity player) {
        return getInventory(player).inventory.size();
    }

    public static void resizeInventory(ServerPlayerEntity player, int size) {
        if(player.isCreative()) return;
        setCurrentScrollPos(player, 0);
        getInventory(player).resize(size);
        sendToClient(player, true);
    }

    public static void cloneInventory(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer) {
        if(newPlayer.isCreative() || oldPlayer.isCreative()) return;
        getExtendedPlayer(newPlayer).cloneInventory(getInventory(oldPlayer), getCurrentScrollPos(oldPlayer));
        sendToClient(newPlayer, false);
    }

    public static int getCurrentScrollPos(PlayerEntity player) {
        return getExtendedPlayer(player).getLastScrollPos();
    }

    public static void setCurrentScrollPos(ServerPlayerEntity player, int pos) {
        if(player.isCreative()) return;
        getInventory(player).replaceInventories(getCurrentScrollPos(player)-pos);
        getExtendedPlayer(player).setLastScrollPos(pos);
        sendToClient(player, false);
    }

    public static void sendToClient(ServerPlayerEntity player, boolean shouldRefreshScreen) {
        NbtCompound nbt = new NbtCompound();
        getExtendedPlayer(player).writeExtendedNbtData(nbt);
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeNbt(nbt).writeBoolean(shouldRefreshScreen);
        ServerPlayNetworking.send(player, ExtraSlotsClient.SYNC_MESSAGE_S2C, buf);
    }
}
