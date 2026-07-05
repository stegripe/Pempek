package org.stegripe.pempek.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.stegripe.pempek.PempekConfig;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PempekCommand extends Command {
    public PempekCommand(String name) {
        super(name);
        this.description = "Pempek related commands";
        this.usageMessage = "/pempek [reload | version]";
        this.setPermission("bukkit.command.pempek");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) throws IllegalArgumentException {
        if (args.length == 1) {
            return Stream.of("reload", "version")
                    .filter(arg -> arg.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) return true;

        if (args.length != 1) {
            sender.sendMessage(Component.text("Usage: " + usageMessage, NamedTextColor.RED));
            return false;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            Command.broadcastCommandMessage(sender, Component.text("Please note that this command is not supported and may cause issues", NamedTextColor.RED));
            Command.broadcastCommandMessage(sender, Component.text("If you encounter any issues please use the /stop command to restart your server.", NamedTextColor.RED));

            MinecraftServer console = MinecraftServer.getServer();
            PempekConfig.init((File) console.options.valueOf("pempek-settings"));
            for (ServerLevel level : console.getAllLevels()) {
                level.pempekConfig.init();
                level.resetBreedingCooldowns(); // Pempek - Add adjustable breeding cooldown to config
            }
            console.server.reloadCount++;

            Command.broadcastCommandMessage(sender, Component.text("Pempek config reload complete.", NamedTextColor.GREEN));
        } else if (args[0].equalsIgnoreCase("version")) {
            Command verCmd = org.bukkit.Bukkit.getServer().getCommandMap().getCommand("version");
            if (verCmd != null) {
                return verCmd.execute(sender, commandLabel, new String[0]);
            }
        } else {
            sender.sendMessage(Component.text("Usage: " + usageMessage, NamedTextColor.RED));
            return false;
        }

        return true;
    }
}
