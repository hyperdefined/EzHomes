package lol.hyper.ezhomes.commands;

import lol.hyper.ezhomes.EzHomes;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

public class CommandReload implements CommandExecutor {

    private final EzHomes ezHomes;

    public CommandReload(EzHomes ezHomes) {
        this.ezHomes = ezHomes;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("ezhomes.reload") || sender.isOp()) {
            ezHomes.config = YamlConfiguration.loadConfiguration(ezHomes.configFile);
            sender.sendMessage(ChatColor.GREEN + "Config was reloaded!");
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have permission to reload!");
        }
        return true;
    }
}
