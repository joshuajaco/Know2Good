package fallen.pentagon.know2good;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerItemConsumeListener implements Listener {
  private final Know2Good plugin;

  public PlayerItemConsumeListener(Know2Good plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onPlayerInteract(PlayerItemConsumeEvent event) {
    ItemStack item = event.getItem();

    ItemMeta meta = item.getItemMeta();

    if (!meta.isUnbreakable()) return;

    ConfigurationSection itemsConfig = plugin.getConfig().getConfigurationSection("items");

    if (itemsConfig == null) return;

    Player player = event.getPlayer();

    itemsConfig.getKeys(false).forEach(key -> {
      ConfigurationSection itemConfig = itemsConfig.getConfigurationSection(key);

      if (itemConfig == null) return;

      String trigger = itemConfig.getString("trigger");
      if (trigger == null || !trigger.equals("eat")) return;

      String name = itemConfig.getString("name");
      if (name == null) return;

      if (meta.getDisplayName().contains(name)) {
        itemConfig
          .getStringList("commands")
          .stream()
          .map(cmd -> cmd.replace("{{player}}", player.getName()))
          .forEach(cmd -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd));

        if (itemConfig.getBoolean("consume")) {
          item.setAmount(item.getAmount() - 1);
        }
      }
    });
  }
}
