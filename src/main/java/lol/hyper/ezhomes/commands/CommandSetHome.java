package lol.hyper.ezhomes.commands;

import lol.hyper.ezhomes.EzHomes;
import lol.hyper.ezhomes.HomeManagement;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandSetHome implements CommandExecutor {

    private final EzHomes ezHomes;
    private final HomeManagement homeManagement;

    public CommandSetHome(EzHomes ezHomes, HomeManagement homeManagement) {
        this.ezHomes = ezHomes;
        this.homeManagement = homeManagement;
    }

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
                    if (HomeManagement.getPlayerHomes(player.getUniqueId()).size() != ezHomes.config.getInt("total-homes") || player.hasPermission("ezhomes.bypasslimit")) {
                        homeManagement.createHome(player.getUniqueId(), args[0]);
                        sender.sendMessage(ChatColor.GREEN + "Home set.");
                    } else {
                        player.sendMessage(ChatColor.RED + "You can only have a maximum of " + ezHomes.config.getInt("total-homes") + " homes.");
                    }
                } else {
                    homeManagement.createHome(player.getUniqueId(), args[0]);
                    sender.sendMessage(ChatColor.GREEN + "Home set.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid syntax. To set a home, simply do \"/sethome <home name>\"");
            }
        }
        return true;
    }
}
