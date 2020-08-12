package lol.hyper.ezhomes;

import lol.hyper.ezhomes.commands.CommandHome;
import lol.hyper.ezhomes.commands.CommandHomes;
import lol.hyper.ezhomes.commands.CommandSetHome;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class EzHomes extends JavaPlugin {

    private static EzHomes instance;
    public File configFile = new File(this.getDataFolder(), "config.yml");
    public Path homesPath = Paths.get(this.getDataFolder() + File.separator + "data");

    public static EzHomes getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        if (!configFile.exists()) {
            this.saveResource("config.yml", true);
            Bukkit.getLogger().info("[EzHomes] Copying default config!");
        }
        if (!Files.exists(homesPath)) {
            try {
                Files.createDirectory(homesPath);
            } catch (IOException e) {
                Bukkit.getLogger().severe("Unable to create folder " + homesPath.toString() + "! Please make the folder manually or check folder permissions!");
                e.printStackTrace();
            }
        }
        this.getCommand("sethome").setExecutor(new CommandSetHome());
        this.getCommand("home").setExecutor(new CommandHome());
        this.getCommand("homes").setExecutor(new CommandHomes());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
