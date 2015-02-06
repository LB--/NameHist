package com.lb_stuff.namehist.command;

import com.lb_stuff.bukkit.command.PluginInfoCommand;
import com.lb_stuff.namehist.NameHistPlugin;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NameHistCommand extends TabbableHistoricalCommand
{
	public NameHistCommand(NameHistPlugin plugin)
	{
		super(plugin);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args)
	{
		List<String> ret = new ArrayList<>();
		if(args.length == 1)
		{
			String start = args[0].toLowerCase();
			for(Player p : Bukkit.getOnlinePlayers())
			{
				if(p.getName().toLowerCase().startsWith(start))
				{
					ret.add(p.getName());
				}
			}
			for(OfflinePlayer p : Bukkit.getOfflinePlayers())
			{
				if(p.getName().toLowerCase().startsWith(start))
				{
					ret.add(p.getName());
				}
			}
		}
		else if(args.length == 2)
		{
			ret.add("2/3/2015"); //day before renames were enabled
		}
		return ret;
	}
	private PluginInfoCommand pic = new PluginInfoCommand(inst);
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(args.length == 0)
		{
			pic.onCommand(sender, cmd, label, args);
		}
		else if(args.length == 1)
		{
			//TODO
		}
		else if(args.length == 2)
		{
			//TODO
		}
		return false;
	}
}
