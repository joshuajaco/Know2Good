package fallen.pentagon.know2good;

import org.bukkit.NamespacedKey;

public class WorldTeleporter {
  private final Know2Good plugin;
  private int taskId;

  public WorldTeleporter(Know2Good plugin) {
    this.plugin = plugin;
  }

  public void start() {
    var teleportConfig = plugin.getConfig().getConfigurationSection("teleport");

    if (teleportConfig == null) return;

    var checkEvery = teleportConfig.getLong("check_every_n_ticks");
    var overWorldAnchor = teleportConfig.getInt("overworld_anchor");
    var endAnchor = teleportConfig.getInt("end_anchor");

    var server = plugin.getServer();
    var advancement = server.getAdvancement(NamespacedKey.minecraft("story/enter_the_end"));

    if (advancement == null) return;

    var overWorld = server.getWorlds().get(0);
    var end = server.getWorlds().get(2);

    taskId = server.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
      overWorld.getPlayers().forEach(player -> {
        var location = player.getLocation();
        if (location.getBlockY() > overWorldAnchor && player.getAdvancementProgress(advancement).isDone()) {
          var velocity = player.getVelocity().clone();
          var newLocation = location.clone();
          newLocation.setWorld(end);
          newLocation.setY(endAnchor + (location.getBlockY() - overWorldAnchor));
          player.teleport(newLocation);
          player.setVelocity(velocity);
        }
      });

      end.getPlayers().forEach(player -> {
        var location = player.getLocation();
        if (location.getBlockY() < endAnchor && player.getAdvancementProgress(advancement).isDone()) {
          var velocity = player.getVelocity().clone();
          var newLocation = location.clone();
          newLocation.setWorld(overWorld);
          newLocation.setY(overWorldAnchor - (endAnchor - location.getBlockY()));
          player.teleport(newLocation);
          player.setVelocity(velocity);
        }
      });
    }, 0L, checkEvery);
  }

  public void restart() {
    plugin.getServer().getScheduler().cancelTask(taskId);
    start();
  }
}
