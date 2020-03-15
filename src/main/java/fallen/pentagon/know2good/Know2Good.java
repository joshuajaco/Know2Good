package fallen.pentagon.know2good;

import org.bukkit.plugin.java.JavaPlugin;

public class Know2Good extends JavaPlugin {
  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
    getConfig().options().copyDefaults(true);
    saveConfig();
  }
}
