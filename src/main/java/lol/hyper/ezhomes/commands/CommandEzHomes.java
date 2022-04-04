package lol.hyper.ezhomes.commands;

import lol.hyper.ezhomes.EzHomes;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CommandEzHomes implements TabExecutor {

    private final EzHomes ezHomes;
    private final Component aboutPlugin;

    public CommandEzHomes (EzHomes ezHomes) {
        this.ezHomes = ezHomes;
        this.aboutPlugin = ezHomes.miniMessage.deserialize("<green>EzHomes version " + ezHomes.getDescription().getVersion() + " . Created by hyperdefined.</green>");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            ezHomes.getAdventure().sender(sender).sendMessage(aboutPlugin);
            return true;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("ezhomes.reload")) {
                    ezHomes.loadConfig();
                    ezHomes.getAdventure().sender(sender).sendMessage(ezHomes.getMessage("config-reloaded", null));
                } else {
                    ezHomes.getAdventure().sender(sender).sendMessage(ezHomes.getMessage("no-perms", null));
                }
            } else {
                ezHomes.getAdventure().sender(sender).sendMessage(aboutPlugin);
            }
            return true;
        }
        ezHomes.getAdventure().sender(sender).sendMessage(aboutPlugin);
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
