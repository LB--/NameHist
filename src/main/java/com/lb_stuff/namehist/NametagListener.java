package com.lb_stuff.namehist;


import org.kitteh.tag.AsyncPlayerReceiveNameTagEvent;
import org.kitteh.tag.TagAPI;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static org.bukkit.ChatColor.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/*default*/ class PartyNametagListener implements OptionalComponent, Listener
{
	private final NameHistPlugin inst;
	public PartyNametagListener(NameHistPlugin plugin)
	{
		inst = plugin;
	}

	private boolean started = false;
	@Override
	public boolean isStarted()
	{
		return started;
	}
	@Override
	public void start()
	{
		if(isStarted())
		{
			return;
		}
		//TODO
		Bukkit.getPluginManager().registerEvents(this, inst);
		refreshAll();
		started = true;
	}
	@Override
	public void stop()
	{
		if(!isStarted())
		{
			return;
		}
		HandlerList.unregisterAll(this);
		//TODO
		refreshAll();
		started = false;
	}

	private final ConcurrentHashMap<UUID, String> partiers = new ConcurrentHashMap<>();
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onNametagReceive(AsyncPlayerReceiveNameTagEvent e)
	{
		String pof = partiers.get(e.getNamedPlayer().getUniqueId());
		String pfor = partiers.get(e.getPlayer().getUniqueId());
		if(pof != null && pfor != null)
		{
			if(pof.equals(pfor))
			{
				e.setTag(""+GREEN+e.getTag());
			}
			else
			{
				e.setTag(""+RED+e.getTag());
			}
		}
	}

	public void refreshAll()
	{
		Set<Player> players = new HashSet<>(); //TODO
		players.addAll(Arrays.asList(Bukkit.getOnlinePlayers()));
		if(players.isEmpty())
		{
			return; //TagAPI is a baby and throws an exception instead of ignoring it
		}
		for(Player p : players)
		{
			TagAPI.refreshPlayer(p, players);
		}
	}
	public void refreshFor(UUID uuid)
	{
		OfflinePlayer offp = Bukkit.getOfflinePlayer(uuid);
		if(offp.isOnline())
		{
			final Player onp = offp.getPlayer();
			final Set<Player> players = new HashSet<>(); //TODO
			players.addAll(Arrays.asList(Bukkit.getOnlinePlayers()));
			if(players.isEmpty())
			{
				return; //TagAPI is a baby and throws an exception instead of ignoring it
			}
			Bukkit.getScheduler().runTask(inst, new Runnable(){@Override public void run()
			{
				TagAPI.refreshPlayer(onp, players);
			}});
		}
	}
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();
		//TODO
	}
}
