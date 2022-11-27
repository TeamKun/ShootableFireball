package net.kunmc.lab.shootablefireball;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.kunmc.lab.commandlib.CommandLib;
import net.kunmc.lab.configlib.ConfigCommandBuilder;
import net.kunmc.lab.shootablefireball.command.MainCommand;
import net.kunmc.lab.shootablefireball.entity.UnacceleratableFireball;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Optional;

public final class ShootableFireballPlugin extends JavaPlugin implements Listener {
    private static ShootableFireballPlugin INSTANCE;
    private Config config;

    public static ShootableFireballPlugin getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        this.config = new Config(this);
        CommandLib.register(this, new MainCommand(new ConfigCommandBuilder(config).build()));

        Bukkit.getPluginManager()
              .registerEvents(this, this);
    }

    @Override
    public void onDisable() {
    }

    public void giveFireball(Player target, double speed, float power, int amount) {
        ItemStack fireCharge = new ItemStack(Material.FIRE_CHARGE, amount);
        fireCharge.editMeta(x -> {
            x.displayName(Component.text(String.format("%f %f", speed, power)));
        });
        target.getInventory()
              .addItem(fireCharge);
    }

    @EventHandler
    private void onUseFireball(PlayerInteractEvent e) {
        if (config.enableHand.isFalse()) {
            return;
        }

        if (e.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        if (e.getHand() == EquipmentSlot.OFF_HAND && e.getPlayer()
                                                      .getInventory()
                                                      .getItemInMainHand()
                                                      .getType() == Material.BOW) {
            return;
        }

        ItemStack item = e.getItem();
        if (item == null) {
            return;
        }

        extractFireballInfo(item).ifPresent(x -> {
            shootFireball(e.getPlayer()
                           .getEyeLocation(),
                          e.getPlayer()
                           .getLocation()
                           .getDirection(),
                          x.speed,
                          x.power);
            e.getPlayer()
             .setCooldown(Material.FIRE_CHARGE, config.coolDown.value());

            GameMode gameMode = e.getPlayer()
                                 .getGameMode();
            if (gameMode == GameMode.SURVIVAL || gameMode == GameMode.ADVENTURE || config.creativeConsumesItem.isTrue()) {
                e.getItem()
                 .setAmount(e.getItem()
                             .getAmount() - 1);
            }
        });
    }

    @EventHandler
    private void onUseArrowWithFireball(EntityShootBowEvent e) {
        if (config.enableArrow.isFalse()) {
            return;
        }

        EntityEquipment equipmentSlot = e.getEntity()
                                         .getEquipment();
        if (equipmentSlot == null) {
            return;
        }

        ItemStack item = equipmentSlot.getItemInOffHand();
        if (item.getType() != Material.FIRE_CHARGE) {
            return;
        }

        extractFireballInfo(item).ifPresent(x -> {
            e.setCancelled(true);

            shootFireball(e.getProjectile()
                           .getLocation(),
                          e.getProjectile()
                           .getVelocity(),
                          x.speed,
                          x.power);

            if (e.getEntity() instanceof Player) {
                GameMode gameMode = ((Player) e.getEntity()).getGameMode();
                if (gameMode == GameMode.SURVIVAL || gameMode == GameMode.ADVENTURE || config.creativeConsumesItem.isTrue()) {
                    item.setAmount(item.getAmount() - 1);
                }

                ((Player) e.getEntity()).updateInventory();
            }
        });
    }

    private Optional<FireballInfo> extractFireballInfo(ItemStack item) {
        String[] split = item.getItemMeta()
                             .getDisplayName()
                             .split(" ");
        if (split.length == 0) {
            return Optional.empty();
        }

        double speed;
        try {
            speed = Double.parseDouble(split[0]);
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }

        float power = 1.0F;
        try {
            power = Float.parseFloat(split[1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignore) {
        }

        return Optional.of(new FireballInfo(speed, power));
    }

    public void shootFireball(Location location, Vector direction, double speed, float power) {
        ((CraftWorld) location.getWorld()).addEntity(new UnacceleratableFireball(location, direction.multiply(speed)),
                                                     CreatureSpawnEvent.SpawnReason.CUSTOM,
                                                     x -> {
                                                         ((Fireball) x).setYield(power);
                                                     });
    }

    @RequiredArgsConstructor
    @Getter
    @Accessors(fluent = true)
    public static class FireballInfo {
        private final double speed;
        private final float power;
    }
}
