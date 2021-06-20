package fallen.pentagon.know2good;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Know2Good extends JavaPlugin {
  private WorldTeleporter worldTeleporter;

  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
    getServer().getPluginManager().registerEvents(new PlayerItemConsumeListener(this), this);
    getConfig().options().copyDefaults(true);
    saveConfig();
    worldTeleporter = new WorldTeleporter(this);
    worldTeleporter.start();
  }

  @Override
  public boolean onCommand(
      CommandSender _sender,
      Command _command,
      String _label,
      String[] _args
  ) {
    reloadConfig();
    worldTeleporter.restart();
    return true;
  }
}
