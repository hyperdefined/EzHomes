package lol.hyper.ezhomes.commands;

import lol.hyper.ezhomes.EzHomes;
import lol.hyper.ezhomes.HomeManagement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class CommandSetHome implements CommandExecutor {
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
                        if (HomeManagement.getPlayerHomes(player.getUniqueId()).size() != EzHomes.getInstance().config.getInt("total-homes")) {
                            HomeManagement.createHome(player.getUniqueId(), player.getLocation(), args[0]);
                            sender.sendMessage(ChatColor.GREEN + "Set new home " + args[0] + "!");
                        } else {
                            player.sendMessage(ChatColor.RED + "You can only have a maximum of " + EzHomes.getInstance().config.getInt("total-homes") + " homes.");
                        }
                    } else {
                        HomeManagement.createHome(player.getUniqueId(), player.getLocation(), args[0]);
                        sender.sendMessage(ChatColor.GREEN + "Set new home " + args[0] + "!");
                    }
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                    sender.sendMessage(ChatColor.RED + "Unable to create home! Please check your console for more information.");
                    Bukkit.getLogger().severe("Error reading home data for player: " + player.getName());
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid syntax. To set a home, simply do \"/sethome <home name>\"");
            }
        }
        return true;
    }
}
