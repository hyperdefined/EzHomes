package lol.hyper.ezhomes.commands;

import io.papermc.lib.PaperLib;
import lol.hyper.ezhomes.EzHomes;
import lol.hyper.ezhomes.HomeManagement;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CommandHome implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("You must be a player for this command!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "You must specify a home!");
        } else {
            if (args.length == 1){
                ArrayList<String> playerHomes = HomeManagement.getPlayerHomes(player);
                if (playerHomes != null) {
                    if (HomeManagement.canPlayerTeleport(player) || player.hasPermission("ezhomes.bypasscooldown")) {
                        if (playerHomes.contains(args[0])) {
                            PaperLib.teleportAsync(player, HomeManagement.getHomeLocation(player, args[0]));
                            player.sendMessage(ChatColor.GREEN + "Whoosh!");
                            EzHomes.getInstance().teleportCooldowns.put(player, System.nanoTime());
                        } else {
                            player.sendMessage(ChatColor.RED + "That home does not exist.");
                        }
                    } else {
                        long timeLeft = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - EzHomes.getInstance().teleportCooldowns.get(player));
                        long configTime = EzHomes.getInstance().config.getInt("teleport-cooldown");
                        player.sendMessage(ChatColor.RED + "You must wait " + (configTime - timeLeft) + " seconds to teleport.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have any homes set.");
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        Player player = (Player) sender;
        return HomeManagement.getPlayerHomes(player);
    }
}
