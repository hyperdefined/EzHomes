package lol.hyper.ezhomes.commands;

import lol.hyper.ezhomes.EzHomes;
import lol.hyper.ezhomes.HomeManagement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class CommandWhere implements CommandExecutor {
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
                try {
                    if (HomeManagement.getPlayerHomes(player.getUniqueId()) != null) {
                        if (HomeManagement.getPlayerHomes(player.getUniqueId()).contains(args[0])) {
                            Location home = HomeManagement.getHomeLocation(player.getUniqueId(), args[0]);
                            sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                            sender.sendMessage(ChatColor.GOLD + args[0] + "'s location:");
                            sender.sendMessage(ChatColor.YELLOW + "World: " + ChatColor.GOLD + home.getWorld().getName());
                            sender.sendMessage(ChatColor.YELLOW + "X: " + ChatColor.GOLD + home.getX());
                            sender.sendMessage(ChatColor.YELLOW + "Y: " + ChatColor.GOLD + home.getY());
                            sender.sendMessage(ChatColor.YELLOW + "Y: " + ChatColor.GOLD + home.getZ());
                            sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                        } else {
                            sender.sendMessage(ChatColor.RED + "That home does not exist.");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You do not have any homes.");
                    }
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                    sender.sendMessage(ChatColor.RED + "Unable to create home! Please check your console for more information.");
                    Bukkit.getLogger().severe("Error reading home data for player: " + player.getName());
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid syntax. To see where a home is, simply do \"/where <home name>\"");
            }
        }
        return true;
    }
}
