package fallen.pentagon.know2good;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Know2Good extends JavaPlugin {
  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
    getServer().getPluginManager().registerEvents(new PlayerItemConsumeListener(this), this);
    getConfig().options().copyDefaults(true);
    saveConfig();

    var teleportConfig = getConfig().getConfigurationSection("teleport");

    if (teleportConfig == null) return;

    var checkEvery = teleportConfig.getLong("check_every_n_ticks");

    var overWorldConfig = teleportConfig.getConfigurationSection("overworld_to_end");

    if (overWorldConfig == null) return;

    var overWorldFrom = overWorldConfig.getInt("from");
    var overWorldTo = overWorldConfig.getInt("to");

    var endConfig = teleportConfig.getConfigurationSection("end_to_overworld");

    if (endConfig == null) return;

    var endFrom = endConfig.getInt("from");
    var endTo = endConfig.getInt("to");

    var overWorld = getServer().getWorlds().get(0);
    var end = getServer().getWorlds().get(2);
    getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
      @Override
      public void run() {
        overWorld.getPlayers().forEach(player -> {
          var location = player.getLocation();
          if (location.getBlockY() > overWorldFrom) {
            var velocity = player.getVelocity().clone();
            var newLocation = location.clone();
            newLocation.setWorld(end);
            newLocation.setY(overWorldTo);
            player.teleport(newLocation);
            player.setVelocity(velocity);
            player.sendMessage("Teleported to end");
          }
        });

        end.getPlayers().forEach(player -> {
          var location = player.getLocation();
          if (location.getBlockY() < endFrom) {
            var velocity = player.getVelocity().clone();
            var newLocation = location.clone();
            newLocation.setWorld(overWorld);
            newLocation.setY(endTo);
            player.teleport(newLocation);
            player.setVelocity(velocity);
            player.sendMessage("Teleported to overworld");
          }
        });
      }
    }, 0L, checkEvery);

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
