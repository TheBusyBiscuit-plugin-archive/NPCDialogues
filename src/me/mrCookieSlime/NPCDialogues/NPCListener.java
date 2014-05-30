package me.mrCookieSlime.NPCDialogues;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class NPCListener implements Listener {
	
	public main plugin;
	
	public NPCListener(main instance) {
		plugin = instance;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onHurt(EntityDamageEvent e) {
		if (main.npcs.contains(e.getEntity())) {
			e.setCancelled(true);
		}
		if (e instanceof EntityDamageByEntityEvent) {
			if (main.npcs.contains(((EntityDamageByEntityEvent) e).getDamager())) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onUnload(ChunkUnloadEvent e) {
		Chunk c = e.getChunk();
		for (Entity n: c.getEntities())  {
			if (main.npcs.contains(n)) {
				NPC.despawn(main.npcs.indexOf(n));
			}
		}
	}
	
	@EventHandler
	public void onLoad(ChunkLoadEvent e) {
		Chunk c = e.getChunk();
		for (int i = 0; i < main.npcs.size(); i++) {
			if (main.npcs.get(i) == null) {
				FileConfiguration cfg = YamlConfiguration.loadConfiguration(main.file);
				if (c == new Location(Bukkit.getWorld(cfg.getString(i + ".WORLD")), cfg.getDouble(i + ".X"), cfg.getDouble(i + ".Y"), cfg.getDouble(i + ".Z")).getChunk()) {
					NPC.spawn(i);
				}
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof LivingEntity) {
			LivingEntity n = (LivingEntity) e.getRightClicked();
			if (main.npcs.contains(n)) {
				
				int i = main.npcs.indexOf(n);
				FileConfiguration cfg = YamlConfiguration.loadConfiguration(main.file);
				
				String name = "";
				
				if (n.getCustomName() != null) {
					name = n.getCustomName() + ChatColor.RESET + " : ";
				}
				
				if (cfg.contains(i + ".dialogue")) {
					e.setCancelled(true);
					e.getPlayer().sendMessage(name + cfg.getString(i + ".dialogue"));
				}
				if (cfg.contains(i + ".command")) {
					e.setCancelled(true);
					e.getPlayer().performCommand(cfg.getString(i + ".command"));
				}
			}
		}
	}

}
