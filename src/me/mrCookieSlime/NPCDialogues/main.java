package me.mrCookieSlime.NPCDialogues;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class main extends JavaPlugin {
	
	public static String prefix = ChatColor.GRAY + "[" + ChatColor.GREEN + "NPCDialogues" + ChatColor.GRAY + "] ";
	public static List<LivingEntity> npcs = new ArrayList<LivingEntity>();
	public static List<EntityType> supported = new ArrayList<EntityType>();
	
	public static File file = new File("data-storage/NPCDialogues/NPCs.yml");
	
	@Override
	public void onEnable() {
		System.out.println("[NPCDialogues] " + "NPCDialogues v" + getDescription().getVersion() + " enabled!");
		
		new NPCListener(this);
		
		supported.add(EntityType.VILLAGER);
		supported.add(EntityType.ZOMBIE);
		supported.add(EntityType.SKELETON);
		
		getCommand("npc").setExecutor(this);
		
		Bukkit.getScheduler().runTaskTimer(this, new BukkitRunnable() {
			
			@Override
			public void run() {
				for (int i = 0; i < npcs.size(); i++) {
					FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
					npcs.get(i).teleport(new Location(Bukkit.getWorld(cfg.getString(i + ".WORLD")), cfg.getDouble(i + ".X"), cfg.getDouble(i + ".Y"), cfg.getDouble(i + ".Z")));
				}
			}
		}, 0L, 200L);
	}
	
	@Override
	public void onDisable() {
		System.out.println("NPCDialogues v" + getDescription().getVersion() + " disabled!");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("npc")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (args.length == 2 && args[0].equalsIgnoreCase("create")) {
					if (supported.contains(EntityType.valueOf(args[1].toUpperCase()))) {
						FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
						
						for (int i = 0; i < 101; i++) {
							if (!cfg.contains(String.valueOf(i))) {
								cfg.set(i + ".type", args[1].toUpperCase());
								cfg.set(i + ".X", p.getLocation().getX());
								cfg.set(i + ".Y", p.getLocation().getY());
								cfg.set(i + ".Z", p.getLocation().getZ());
								cfg.set(i + ".WORLD", p.getLocation().getWorld().getName());
								p.sendMessage(ChatColor.GREEN + "NPC " + ChatColor.DARK_GREEN + "(ID: " + i + ") " + ChatColor.GREEN + "created");
								break;
							}
						}
						
						try {
							cfg.save(file);
						} catch (IOException e) {
						}
						
						LivingEntity n = (LivingEntity) p.getWorld().spawnEntity(p.getLocation(), EntityType.valueOf(args[1].toUpperCase()));
						n.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 999999999, 999999999));
						n.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 999999999, -999999999));
						npcs.add(n);
					}
					else {
						p.sendMessage(prefix + ChatColor.DARK_RED + args[1].toUpperCase() + ChatColor.RED + " is not supported. Do /npc list types");
					}
				}
				else if (args.length == 2 && args[0].equalsIgnoreCase("list")) {
					if (args[1].equalsIgnoreCase("types")) {
						for (EntityType type: supported) {
							p.sendMessage(ChatColor.AQUA + type.toString());
						}
					}
					else if (args[1].equalsIgnoreCase("ids")) {
						FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
						for (int i = 0; i < 101; i++) {
							if (!cfg.contains(String.valueOf(i))) {
								break;
							}
							else {
								p.sendMessage(ChatColor.AQUA + String.valueOf(i));
							}
						}
					}
				}
				else if (args.length >= 3 && args[0].equalsIgnoreCase("name")) {
					boolean number = true;
					try {
						Integer.parseInt(args[1]);
					} catch(NumberFormatException x) {
						number = false;
					}
					
					if (number) {
						if ((npcs.size() - 1) <= Integer.parseInt(args[1])) {
							String name = "";
							for (int i = 2; i < args.length; i++) {
								if (i == 2) {
									name = args[i];
								}
								else {
									name = name + " " + args[i];
								}
							}
							
							name = ChatColor.translateAlternateColorCodes('&', name);
							npcs.get(Integer.parseInt(args[1])).setCustomName(name);
							npcs.get(Integer.parseInt(args[1])).setCustomNameVisible(true);
							p.sendMessage(ChatColor.GREEN + "Name set to: " + ChatColor.RESET + name);
							
							FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
							cfg.set(args[1] + ".name", name);
							try {
								cfg.save(file);
							} catch (IOException e) {
							}
							
						}
						else {
							p.sendMessage(prefix + ChatColor.DARK_RED + args[1] + ChatColor.RED + " is not a valid ID");
						}
					}
					else {
						p.sendMessage(prefix + ChatColor.DARK_RED + args[1] + ChatColor.RED + " is not a Number");
					}
				}
				else if (args.length >= 3 && args[0].equalsIgnoreCase("dialogue")) {
					
					boolean number = true;
					try {
						Integer.parseInt(args[1]);
					} catch(NumberFormatException x) {
						number = false;
					}
					
					if (number) {
						if ((npcs.size() - 1) <= Integer.parseInt(args[1])) {
							String text = "";
							for (int i = 2; i < args.length; i++) {
								if (i == 2) {
									text = args[i];
								}
								else {
									text = text + " " + args[i];
								}
							}
							
							text = ChatColor.translateAlternateColorCodes('&', text);
							p.sendMessage(ChatColor.GREEN + "Text set to: " + ChatColor.RESET + text);
							
							FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
							cfg.set(args[1] + ".dialogue", text);
							try {
								cfg.save(file);
							} catch (IOException e) {
							}
							
						}
						else {
							p.sendMessage(prefix + ChatColor.DARK_RED + args[1] + ChatColor.RED + " is not a valid ID");
						}
					}
					else {
						p.sendMessage(prefix + ChatColor.DARK_RED + args[1] + ChatColor.RED + " is not a Number");
					}
				}
				else if (args.length >= 3 && args[0].equalsIgnoreCase("command")) {
					
					boolean number = true;
					try {
						Integer.parseInt(args[1]);
					} catch(NumberFormatException x) {
						number = false;
					}
					
					if (number) {
						if ((npcs.size() - 1) <= Integer.parseInt(args[1])) {
							String text = "";
							for (int i = 2; i < args.length; i++) {
								if (i == 2) {
									text = args[i];
								}
								else {
									text = text + " " + args[i];
								}
							}
							
							text = ChatColor.translateAlternateColorCodes('&', text);
							p.sendMessage(ChatColor.GREEN + "Command set to: " + ChatColor.RESET + text);
							
							FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
							cfg.set(args[1] + ".command", text);
							try {
								cfg.save(file);
							} catch (IOException e) {
							}
						}
						else {
							p.sendMessage(prefix + ChatColor.DARK_RED + args[1] + ChatColor.RED + " is not a valid ID");
						}
					}
					else {
						p.sendMessage(prefix + ChatColor.DARK_RED + args[1] + ChatColor.RED + " is not a Number");
					}
				}
				else {
					p.sendMessage("");
					p.sendMessage(ChatColor.GOLD + "NPCDialogues v" + getDescription().getVersion() + " by mrCookieSlime");
					p.sendMessage("");
					p.sendMessage(ChatColor.YELLOW + "/npc create <Type> - Creates an NPC");
					p.sendMessage(ChatColor.YELLOW + "/npc list <types/ids> - Lists all available Types or all used IDs");
					p.sendMessage(ChatColor.YELLOW + "/npc name <ID> <Name> - Sets an NPCs' display Name");
					p.sendMessage(ChatColor.YELLOW + "/npc dialogue <ID> <Text>");
					p.sendMessage(ChatColor.YELLOW + "/npc command <ID> <Command>");
					p.sendMessage("");
				}
			}
			else {
				sender.sendMessage(prefix + ChatColor.RED + "Only Players can perform this Command!");
			}
		}
		
		return true;
	}

}
