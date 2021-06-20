package fallen.pentagon.know2good;

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

    var overWorldConfig = teleportConfig.getConfigurationSection("overworld_to_end");

    if (overWorldConfig == null) return;

    var overWorldFrom = overWorldConfig.getInt("from");
    var overWorldTo = overWorldConfig.getInt("to");

    var endConfig = teleportConfig.getConfigurationSection("end_to_overworld");

    if (endConfig == null) return;

    var endFrom = endConfig.getInt("from");
    var endTo = endConfig.getInt("to");

    var overWorld = plugin.getServer().getWorlds().get(0);
    var end = plugin.getServer().getWorlds().get(2);
    taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
      overWorld.getPlayers().forEach(player -> {
        var location = player.getLocation();
        if (location.getBlockY() > overWorldFrom) {
          var velocity = player.getVelocity().clone();
          var newLocation = location.clone();
          newLocation.setWorld(end);
          newLocation.setY(overWorldTo);
          player.teleport(newLocation);
          player.setVelocity(velocity);
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
        }
      });
    }, 0L, checkEvery);
  }

  public void restart() {
    plugin.getServer().getScheduler().cancelTask(taskId);
    start();
  }
}
