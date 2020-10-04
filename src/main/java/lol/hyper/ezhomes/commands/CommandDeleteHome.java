package lol.hyper.ezhomes.commands;

import lol.hyper.ezhomes.HomeManagement;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandDeleteHome implements CommandExecutor {
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
                if (HomeManagement.getPlayerHomes(player) != null) {
                    if (HomeManagement.getPlayerHomes(player).contains(args[0])) {
                        HomeManagement.deleteHome(player, args[0]);
                        player.sendMessage(ChatColor.GREEN + "Home was deleted.");
                    } else {
                        player.sendMessage(ChatColor.RED + "That home does not exist.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have any homes.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid syntax. To delete a home, simply do \"/delhome <home name>\"");
            }
        }
        return true;
    }
}
