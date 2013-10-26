package com.darktidegames.celeo;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.darktidegames.empyrean.C;

/**
 * 
 * @author Celeo
 */
public class Lock
{

	private String owner = null;
	private List<String> canAccess = new ArrayList<String>();
	private Location location = null;

	/**
	 * You'd better set the fields yourself, or use <i>fromString(String)</i>
	 */
	public Lock()
	{
	}

	/**
	 * 
	 * @param owner
	 *            String
	 */
	public Lock(String owner)
	{
		this.owner = owner;
	}

	/**
	 * Returns a String usable by <i>Lock.fromString(String)</i>
	 */
	@Override
	public String toString()
	{
		return owner + ";" + C.listToString(canAccess) + ";"
				+ C.locationToString(location);
	}

	/**
	 * Constructs this object from a String returned by <i>Lock.toString()</i>
	 * 
	 * @param string
	 */
	public Lock fromString(String string)
	{
		owner = string.split(";")[0];
		if (string.split(";")[1] != null && !string.split(";")[1].equals(""))
			canAccess = C.stringToList(string.split(";")[1]);
		else
			canAccess = new ArrayList<String>();
		location = C.stringToLocation(string.split(";")[2]);
		return this;
	}

	/*
	 * GET and SET
	 */

	public String getOwner()
	{
		return owner;
	}

	/**
	 * 
	 * @param owner
	 *            String
	 * @return this
	 */
	public Lock setOwner(String owner)
	{
		this.owner = owner;
		return this;
	}

	public List<String> getCanAccess()
	{
		return canAccess;
	}

	/**
	 * 
	 * @param canAccess
	 *            List of String
	 * @return this
	 */
	public Lock setCanAccess(List<String> canAccess)
	{
		this.canAccess = canAccess;
		return this;
	}

	public Location getLocation()
	{
		return location;
	}

	/**
	 * 
	 * @param location
	 *            Location
	 * @return this
	 */
	public Lock setLocation(Location location)
	{
		this.location = location;
		return this;
	}

	/**
	 * 
	 * @param name
	 *            String
	 * @return True if that name can access the locked object
	 */
	public boolean canAccess(String name)
	{
		return owner.equals(name) || canAccess.contains(name);
	}

	public void toggleAccess(String args)
	{
		if (canAccess.contains(args))
			canAccess.remove(args);
		else
			canAccess.add(args);
	}

	public void showInfo(Player player)
	{
		player.sendMessage("§7Owner: §b" + owner + "\n§7CanAccess: §b"
				+ C.listToString(canAccess));
	}

}