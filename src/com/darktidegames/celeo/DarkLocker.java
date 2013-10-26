package com.darktidegames.celeo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DarkLocker extends JavaPlugin implements Listener
{

	private List<Lock> locks = new ArrayList<Lock>();
	private Map<String, Action> actions = new HashMap<String, Action>();

	@Override
	public void onLoad()
	{
		getDataFolder().mkdirs();
		if (!new File(getDataFolder(), "config.yml").exists())
			saveDefaultConfig();
		load();
	}

	private void load()
	{
		reloadConfig();
		for (String str : getConfig().getStringList("locks"))
			locks.add(new Lock().fromString(str));
	}

	@Override
	public void onEnable()
	{
		load();
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("Enabled");
	}

	@Override
	public void onDisable()
	{
		save();
		getLogger().info("Disabled");
	}

	private void save()
	{
		List<String> ret = new ArrayList<String>();
		for (Lock l : locks)
			ret.add(l.toString());
		getConfig().set("locks", ret);
		saveConfig();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (!(sender instanceof Player))
			return false;
		Player player = (Player) sender;
		if (!hasPerms(player, "darklocker.use"))
			return true;
		if (args == null || args.length == 0)
			return doHelp(player);
		String name = player.getName();
		if (args[0].equalsIgnoreCase("set"))
		{
			setPending(player, Action.Type.SET, null);
			player.sendMessage("§bClicked the object that you wish to lock");
		}
		else if (args[0].equalsIgnoreCase("remove"))
		{
			setPending(player, Action.Type.REMOVE, null);
			player.sendMessage("§bClicked the object that you wish to unlock");
		}
		else if (args[0].equalsIgnoreCase("cancel"))
		{
			actions.remove(name);
			player.sendMessage("§aAction cancelled");
		}
		else if (args[0].equalsIgnoreCase("access"))
		{
			if (args.length == 2)
			{
				setPending(player, Action.Type.MODIFY, args[1]);
				player.sendMessage("§bClicked the object that you wish to modify");
			}
			else
				player.sendMessage("§c/lock access [name]");
		}
		else if (args[0].equalsIgnoreCase("info"))
		{
			setPending(player, Action.Type.INFO, null);
			player.sendMessage("§bClicked the object that you wish to examine");
		}
		else if (args[0].equals("-save"))
		{
			save();
			player.sendMessage("§aSaved to config");
		}
		else
			doHelp(player);
		return true;
	}

	private boolean hasPerms(Player player, String node)
	{
		if (!player.hasPermission(node))
		{
			player.sendMessage("§cYou cannot use that");
			return false;
		}
		return true;
	}

	private boolean doHelp(Player player)
	{
		player.sendMessage("§c/lock [set|remove|cancel|access]");
		return true;
	}

	public boolean isLocked(Location location)
	{
		return getLock(location) != null;
	}

	public Lock getLock(Location location)
	{
		for (Lock l : locks)
			if (l.getLocation().equals(location))
				return l;
		return null;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		if (player == null || event.getClickedBlock() == null
				|| event.isCancelled())
			return;
		String name = player.getName();
		Location loc = event.getClickedBlock().getLocation();
		Block block = event.getClickedBlock();
		if (actions.containsKey(name))
		{
			Action action = actions.get(name);
			switch (action.type)
			{
			case SET:
				if (!isLocked(loc))
				{
					Lock l = new Lock(name);
					l.setLocation(loc);
					locks.add(l);
					player.sendMessage("§aThat object is now locked to players");
				}
				else
					player.sendMessage("§cThat is already locked!");
				break;
			case REMOVE:
				if (isLocked(loc))
				{
					Lock l = getLock(loc);
					locks.remove(l);
					player.sendMessage("§aLock removed successfully");
				}
				else
					player.sendMessage("§cThat is not locked");
				break;
			case MODIFY:
				if (isLocked(loc))
				{
					Lock l = getLock(loc);
					l.toggleAccess(action.args);
					player.sendMessage("§aLock modified successfully");
				}
				else
					player.sendMessage("§cThat is not locked");
				break;
			case INFO:
				Lock l = getLock(loc);
				l.showInfo(player);
				break;
			case CHANGEOWNER:
				player.sendMessage("§cFeature not yet implemented");
				break;
			}
			actions.remove(name);
			return;
		}
		if (block.getType().equals(Material.CHEST))
		{
			World w = loc.getWorld();
			double x = loc.getX();
			double y = loc.getY();
			double z = loc.getZ();
			if (isLocked(new Location(w, x + 1, y, z)))
				loc = new Location(w, x + 1, y, z);
			if (isLocked(new Location(w, x - 1, y, z)))
				loc = new Location(w, x - 1, y, z);
			if (isLocked(new Location(w, x, y, z + 1)))
				loc = new Location(w, x, y, z + 1);
			if (isLocked(new Location(w, x, y, z - 1)))
				loc = new Location(w, x, y, z - 1);
		}
		if (getLock(loc) != null && !getLock(loc).canAccess(name)
				&& !player.hasPermission("darklocker.bypass"))
		{
			event.setCancelled(true);
			player.sendMessage("§cThat object is locked and cannot be interacted with!");
		}
	}

	private void setPending(Player player, Action.Type type, String args)
	{
		actions.put(player.getName(), new Action(player, type, args));
	}

}