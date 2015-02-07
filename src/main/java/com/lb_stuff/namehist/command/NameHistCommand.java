package com.lb_stuff.namehist.command;

import com.lb_stuff.bukkit.command.PluginInfoCommand;
import com.lb_stuff.namehist.NameHistPlugin;
import com.lb_stuff.namehist.api.IHistoryEnthusiast;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.UUID;

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
			ret.add("2015-02-03"); //day before renames were enabled
		}
		return ret;
	}
	private PluginInfoCommand pic = new PluginInfoCommand(inst);
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, final String[] args)
	{
		if(args.length == 0)
		{
			pic.onCommand(sender, cmd, label, args);
		}
		else
		{
			IHistoryEnthusiast enthusiast = new IHistoryEnthusiast()
			{
				@Override
				public void holdBreath()
				{
					inst.tellMessage(sender, "please-wait-name", args[0]);
				}
				@Override
				public void learn(UUID uuid, SortedMap<Instant, String> history)
				{
					inst.tell(sender, uuid.toString());
					inst.tellMessage(sender, "history-original", history.get(history.firstKey()));
					for(Map.Entry<Instant, String> h : history.entrySet())
					{
						if(h.getKey() == history.firstKey())
						{
							continue;
						}
						if(h.getKey() == history.lastKey())
						{
							inst.tellMessage(sender, "history-realtime", h.getValue(), h.getKey().toString());
							continue;
						}
						inst.tellMessage(sender, "history-historic", h.getValue(), h.getKey().toString());
					}
					inst.tell(sender, uuid.toString());
				}
				@Override
				public void letDown(Throwable t)
				{
					StringWriter w = new StringWriter();
					t.printStackTrace(new PrintWriter(w));
					inst.tellConsole(""+w);
					inst.tellMessage(sender, "erroneous-errors", args[0]);
				}
			};
			if(args.length == 1)
			{
				inst.getService().tellHistory(args[0], Instant.now(), enthusiast);
			}
			else if(args.length == 2)
			{
				inst.getService().tellHistory(args[0], LocalDate.parse(args[1]).atStartOfDay(ZoneId.systemDefault()).toInstant(), enthusiast);
			}
		}
		return false;
	}
}
