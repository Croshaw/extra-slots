package me.croshaw.extraslots;

import me.croshaw.extraslots.commands.TestCommand;
import me.croshaw.extraslots.utils.Constant;
import me.croshaw.extraslots.utils.InventoryHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

public class ExtraSlots implements ModInitializer {
    public static final String MOD_ID = "extraslots";

    public static final Identifier SYNC_MESSAGE_C2S = id("sync_message_c2s");
    public static final Identifier REQUEST_SYNC_MESSAGE_C2S = id("request_sync_message_c2s");

    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(SYNC_MESSAGE_C2S, (server, player, handler, buf, responseSender) -> {
            InventoryHelper.setCurrentScrollPos(player, buf.readInt());
        });

        ServerPlayNetworking.registerGlobalReceiver(REQUEST_SYNC_MESSAGE_C2S, (server, player, handler, buf, responseSender) -> {
            InventoryHelper.sendToClient(player);
        });
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            if(alive || oldPlayer.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
                InventoryHelper.cloneInventory(oldPlayer, newPlayer);
            } else if(Constant.SaveInventorySizeOnDeath) {
                InventoryHelper.createInventory(newPlayer, InventoryHelper.getSize(oldPlayer));
            }
        });
        CommandRegistrationCallback.EVENT.register(TestCommand::register);
    }
    public static Identifier id(String name) {
        return new Identifier(MOD_ID, name);
    }
}
