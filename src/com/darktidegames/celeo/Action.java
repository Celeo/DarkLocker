package com.darktidegames.celeo;

import org.bukkit.entity.Player;

public class Action
{

	public final Player player;
	public final Action.Type type;
	public final String args;

	public Action(Player player, Action.Type type, String args)
	{
		this.player = player;
		this.type = type;
		this.args = args;
	}

	public enum Type
	{
		SET, REMOVE, MODIFY, INFO, CHANGEOWNER;
	}

}