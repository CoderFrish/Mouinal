package dev.mouinal.commands;

import dev.mouinal.config.MouinalConfig;
import net.minecraft.server.MinecraftServer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.craftbukkit.CraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class MouinalCommand extends BukkitCommand {
    private final MouinalConfig config;

    public MouinalCommand(MouinalConfig config) {
        super("mouinal", "Mouinal server default command", "/<Command>", List.of());
        this.config = config;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            sender.sendMessage("Usage: /mouinal <arg0> <arg1>...");
        } else if (args.length == 1) {
            String arg0 = args[0];

            switch (arg0) {
                case "reload":
                    MinecraftServer server = ((CraftServer) sender.getServer()).getServer();
                    config.reloadConfig();
                    server.server.reloadCount++;
                    sender.sendMessage(text("Config reload complete!", GREEN));

                default:
                    sender.sendMessage(text("This argument is not recognized.", RED));
            }
        }

        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) throws IllegalArgumentException {
        return List.of("reload");
    }
}
