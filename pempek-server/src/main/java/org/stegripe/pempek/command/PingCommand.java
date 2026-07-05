package org.stegripe.pempek.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import org.stegripe.pempek.PempekConfig;

import java.util.Collection;
import java.util.Collections;

public class PingCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ping")
                .requires((listener) -> listener.hasPermission(Permissions.COMMANDS_GAMEMASTER, "bukkit.command.ping"))
                .executes((context) -> execute(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException())))
                .then(Commands.argument("targets", EntityArgument.players())
                        .requires(listener -> listener.hasPermission(Permissions.COMMANDS_GAMEMASTER, "bukkit.command.ping.other"))
                        .executes((context) -> execute(context.getSource(), EntityArgument.getPlayers(context, "targets")))
                )
        );
    }

    private static int execute(CommandSourceStack sender, Collection<ServerPlayer> targets) {
        for (ServerPlayer player : targets) {
            String output = String.format(PempekConfig.pingCommandOutput, player.getGameProfile().name(), player.connection.latency());
            sender.sendSuccess(output, false);
        }
        return targets.size();
    }
}
