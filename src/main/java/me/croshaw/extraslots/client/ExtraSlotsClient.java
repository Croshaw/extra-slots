package me.croshaw.extraslots.client;

import me.croshaw.extraslots.ExtraSlots;
import me.croshaw.extraslots.utils.InventoryHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public class ExtraSlotsClient implements ClientModInitializer {
    public static final Identifier SYNC_MESSAGE_S2C = ExtraSlots.id("sync_message_s2c");
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(SYNC_MESSAGE_S2C, (client, handler, buf, responseSender) -> {
            if(client.player == null) return;
            InventoryHelper.getExtendedPlayer(client.player).readExtendedNbtData(buf.readNbt());
        });
    }
}
