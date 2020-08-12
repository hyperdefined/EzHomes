package lol.hyper.ezhomes.commands;

import lol.hyper.ezhomes.HomeManagement;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

public class CommandHome implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "You must specify a home!");
        } else {
            if (args.length == 1){
                try {
                    ArrayList<String> playerHomes = HomeManagement.getPlayerHomes(player.getUniqueId());
                    if (playerHomes != null) {
                        if (playerHomes.contains(args[0])) {
                            player.teleport(HomeManagement.getHomeLocation(player.getUniqueId(), args[0]));
                            player.sendMessage(ChatColor.GREEN + "Whoosh!");
                        } else {
                            player.sendMessage(ChatColor.RED + "That home does not exist.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You do not have any homes set.");
                    }
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}
