package fallen.pentagon.know2good;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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
    EquipmentSlot hand = event.getHand();

    if (hand == null) return;

    ItemStack item = inventory.getItem(hand);

    if (item == null) return;

    ItemMeta meta = item.getItemMeta();

    if (meta == null || !meta.isUnbreakable()) return;

    ConfigurationSection itemsConfig = plugin.getConfig().getConfigurationSection("items");

    if (itemsConfig == null) return;

    itemsConfig.getKeys(false).forEach(key -> {
      ConfigurationSection itemConfig = itemsConfig.getConfigurationSection(key);

      if (itemConfig == null) return;

      String trigger = itemConfig.getString("trigger");
      if (trigger == null || !trigger.equals("right_click")) return;

      String name = itemConfig.getString("name");
      if (name == null) return;

      if (meta.getDisplayName().contains(name)) {
        event.setCancelled(true);

        itemConfig
          .getStringList("commands")
          .stream()
          .map(cmd -> cmd.replace("{{player}}", player.getName()))
          .forEach(cmd -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd));

        if (itemConfig.getBoolean("consume")) {
          if (itemConfig.contains("usages")) {
            int maxUsages = itemConfig.getInt("usages");
            PersistentDataContainer data = meta.getPersistentDataContainer();
            int usages = data.getOrDefault(new NamespacedKey(plugin, "usages"), PersistentDataType.INTEGER, maxUsages);
            usages--;
            if (usages <= 0) {
              item.setAmount(item.getAmount() - 1);
            } else {
              data.set(new NamespacedKey(plugin, "usages"), PersistentDataType.INTEGER, usages);
              item.setItemMeta(meta);
            }
          } else {
            item.setAmount(item.getAmount() - 1);
          }
        }
      }
    });
  }
}
