package lol.hyper.ezhomes.commands;

import lol.hyper.ezhomes.HomeManagement;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
public class CommandHomes implements CommandExecutor {

    private final HomeManagement homeManagement;

    public CommandHomes(HomeManagement homeManagement) {
        this.homeManagement = homeManagement;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("You must be a player for this command!");
            return true;
        }
        Player player = (Player) sender;
        ArrayList<String> playerHomes = homeManagement.getPlayerHomes(player.getUniqueId());
        if (playerHomes == null || playerHomes.size() == 0) {
            player.sendMessage(ChatColor.RED + "You don't have any homes set! Do /sethome <name> to set a home!");
        } else {
            sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
            player.sendMessage(ChatColor.GOLD + "You currently have these homes:");
            player.sendMessage(ChatColor.YELLOW + String.join(", ", playerHomes));
            sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
        }
        return true;
    }
}
