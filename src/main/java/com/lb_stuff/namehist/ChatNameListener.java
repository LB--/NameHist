package com.lb_stuff.namehist;

import com.lb_stuff.eventfilterservices.ChatFilterService.AsyncMessage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/*default*/ class ChatNameListener implements OptionalComponent, Listener
{
	private final NameHistPlugin inst;
	public ChatNameListener(NameHistPlugin plugin)
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
		Bukkit.getPluginManager().registerEvents(this, inst);
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
		started = false;
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onMessage(final AsyncMessage m)
	{
		//TODO
	}
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();
		//TODO
	}
}
