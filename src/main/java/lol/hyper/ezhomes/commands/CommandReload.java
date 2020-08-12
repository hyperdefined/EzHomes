package lol.hyper.ezhomes.commands;

import lol.hyper.ezhomes.EzHomes;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

public class CommandReload implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        EzHomes.getInstance().config = YamlConfiguration.loadConfiguration(EzHomes.getInstance().configFile);
        sender.sendMessage(ChatColor.GREEN + "Config was reloaded!");
        return true;
    }
}
