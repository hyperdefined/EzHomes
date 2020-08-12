package lol.hyper.ezhomes.commands;

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
import java.util.ArrayList;

public class CommandHomes implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("You must be a player for this command!");
            return true;
        }
        Player player = (Player) sender;
        try {
            ArrayList<String> playerHomes = HomeManagement.getPlayerHomes(player.getUniqueId());
            if (playerHomes == null || playerHomes.size() == 0) {
                player.sendMessage(ChatColor.RED + "You don't have any homes set! Do /sethome <name> to set a home!");
            } else {
                player.sendMessage(ChatColor.GOLD + "You currently have these homes:");
                player.sendMessage(ChatColor.YELLOW + String.join(", ", playerHomes));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "There was an issue reading your home data. Please check your console for more information.");
            Bukkit.getLogger().severe("Error reading home data for player: " + player.getName());
        }
        return true;
    }
}
