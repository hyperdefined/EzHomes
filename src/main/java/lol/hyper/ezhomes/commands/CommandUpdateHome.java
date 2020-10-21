package lol.hyper.ezhomes.commands;

import lol.hyper.ezhomes.HomeManagement;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandUpdateHome implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("You must be a player for this command!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "You must specify a home name!");
        } else {
            if (args.length == 1) {
                if (HomeManagement.getPlayerHomes(player.getUniqueId()) != null) {
                    if (HomeManagement.getPlayerHomes(player.getUniqueId()).contains(args[0])) {
                        HomeManagement.updateHome(player.getUniqueId(), args[0]);
                        player.sendMessage(ChatColor.GREEN + "Updated home.");
                    } else {
                        player.sendMessage(ChatColor.RED + "That home does not exist.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have any homes!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid syntax. To update a home, simply do \"/updatehome <home name>\"");
            }
        }
        return true;
    }
}
