package com.lb_stuff.namehist.command;

import com.lb_stuff.namehist.NameHistPlugin;

import org.bukkit.command.TabCompleter;
import org.bukkit.command.TabExecutor;

public abstract class TabbableHistoricalCommand extends HistoricalCommand implements TabCompleter, TabExecutor
{
	public TabbableHistoricalCommand(NameHistPlugin plugin)
	{
		super(plugin);
	}
}
