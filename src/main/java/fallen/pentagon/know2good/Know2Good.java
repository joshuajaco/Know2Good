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
    getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
      @Override
      public void run() {
        var overWorld = getServer().getWorlds().get(0);
        var end = getServer().getWorlds().get(2);

        overWorld.getPlayers().forEach(player -> {
          var location = player.getLocation();
          if (location.getBlockY() > 320) {
            var velocity = player.getVelocity().clone();
            var newLocation = location.clone();
            newLocation.setWorld(end);
            newLocation.setY(10);
            player.teleport(newLocation);
            player.setVelocity(velocity);
            player.sendMessage("Teleported to end");
          }
        });

        end.getPlayers().forEach(player -> {
          var location = player.getLocation();
          if (location.getBlockY() < -5) {
            var velocity = player.getVelocity().clone();
            var newLocation = location.clone();
            newLocation.setWorld(overWorld);
            newLocation.setY(320);
            player.teleport(newLocation);
            player.setVelocity(velocity);
            player.sendMessage("Teleported to overworld");
          }
        });
      }
    }, 0L, 20L);

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
