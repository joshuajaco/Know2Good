package fallen.pentagon.know2good;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Know2Good extends JavaPlugin {
  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
    getConfig().options().copyDefaults(true);
    saveConfig();
  }

  @Override
  public boolean onCommand(
    CommandSender _sender,
    Command _command,
    String _label,
    String[] _args
  ) {
    reloadConfig();
    return true;
  }
}
