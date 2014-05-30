package me.mrCookieSlime.NPCDialogues;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NPC {
	
	public static void spawn(int id) {
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(main.file);
		LivingEntity n = (LivingEntity) Bukkit.getWorld(cfg.getString(id + ".WORLD")).spawnEntity(new Location(Bukkit.getWorld(cfg.getString(id + ".WORLD")), cfg.getDouble(id + ".X"), cfg.getDouble(id + ".Y"), cfg.getDouble(id + ".Z")), EntityType.valueOf(cfg.getString(id + ".type")));
		n.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 999999999, 999999999));
		n.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 999999999, -999999999));
		
		if (n instanceof Villager) {
			((Villager) n).setProfession(Profession.FARMER);
		}
		
		n.getEquipment().clear();
		
		if (cfg.contains(id + ".name")) {
			n.setCustomNameVisible(true);
			n.setCustomName(cfg.getString(id + ".name"));
		}
		
		if (main.npcs.size() > id) {
			main.npcs.set(id, n);
		}
		else {
			main.npcs.add(n);
		}
	}
	
	public static void despawn(int id) {
		main.npcs.get(id).remove();
		main.npcs.set(id, null);
	}
	
	public static void refresh(int id) {
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(main.file);
		main.npcs.get(id).teleport(new Location(Bukkit.getWorld(cfg.getString(id + ".WORLD")), cfg.getDouble(id + ".X"), cfg.getDouble(id + ".Y"), cfg.getDouble(id + ".Z")));
	}

}
