package fallen.pentagon.know2good;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerInteractListener implements Listener {
  private final Know2Good plugin;

  public PlayerInteractListener(Know2Good plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;

    Player player = event.getPlayer();
    PlayerInventory inventory = player.getInventory();
    ItemStack item = inventory.getItem(inventory.getHeldItemSlot());

    if (item == null) return;

    ItemMeta meta = item.getItemMeta();

    if (!meta.isUnbreakable()) return;

    ConfigurationSection itemsConfig = plugin.getConfig().getConfigurationSection("items");

    if (itemsConfig == null) return;

    itemsConfig.getKeys(false).forEach(key -> {
      ConfigurationSection itemConfig = itemsConfig.getConfigurationSection(key);

      if (itemConfig == null) return;

      if (meta.getDisplayName().equals(itemConfig.getString("name"))) {
        event.setCancelled(true);

        itemConfig
          .getStringList("commands")
          .stream()
          .map(cmd -> cmd.replace("{{player}}", player.getDisplayName()))
          .forEach(cmd -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd));

        if (itemConfig.getBoolean("consume")) {
          item.setAmount(item.getAmount() - 1);
        }
      }
    });
  }
}
