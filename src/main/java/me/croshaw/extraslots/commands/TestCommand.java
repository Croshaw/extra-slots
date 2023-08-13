package me.croshaw.extraslots.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.croshaw.extraslots.utils.InventoryHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class TestCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("extraslots").requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("resize")
                        .then(CommandManager.argument("size", IntegerArgumentType.integer(0,99))
                                .executes(context -> resize(context.getSource(), IntegerArgumentType.getInteger(context, "size"))))));
    }

    private static int resize(ServerCommandSource source, int size) throws CommandSyntaxException {
        InventoryHelper.resizeInventory(source.getPlayer(), size);
        return 1;
    }
}
