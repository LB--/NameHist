package com.lb_stuff.namehist.command;

import com.lb_stuff.namehist.NameHistPlugin;

import org.bukkit.command.CommandExecutor;

public abstract class HistoricalCommand implements CommandExecutor
{
	protected final NameHistPlugin inst;
	public HistoricalCommand(NameHistPlugin plugin)
	{
		inst = plugin;
	}
}
