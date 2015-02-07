package com.lb_stuff.namehist.api;

import com.lb_stuff.namehist.NameHistPlugin;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Provides services to NameHist and other plugins.
 */
public class NameHistService
{
	private final NameHistPlugin inst;
	private final File cacheFolder;
	public NameHistService(NameHistPlugin plugin)
	{
		inst = plugin;
		cacheFolder = new File(inst.getDataFolder(), "feel free to delete this folder sometimes");
		if(cacheFolder.isFile())
		{
			cacheFolder.delete();
		}
		cacheFolder.mkdirs();
		cacheFolder.mkdir();
	}

	public static final Instant DATE_BEFORE_RENAMES = LocalDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault())
		.with(ChronoField.YEAR, 2015)
		.with(ChronoField.MONTH_OF_YEAR, Month.FEBRUARY.getValue())
		.with(ChronoField.DAY_OF_MONTH, 3)
		.toInstant(ZoneOffset.UTC);

	private SortedMap<Instant, String> checkCache(File cacheFile) throws IOException, InvalidConfigurationException, NullPointerException, IllegalStateException, NumberFormatException
	{
		YamlConfiguration cache = new YamlConfiguration();
		cache.load(cacheFile);
		SortedMap<Instant, String> history = new TreeMap<>();
		for(Map<?, ?> h : cache.getMapList("history"))
		{
			String name = h.get("name").toString();
			Object tso = h.get("changedToAt");
			final Instant timestamp;
			if(tso == null)
			{
				if(history.containsKey(DATE_BEFORE_RENAMES))
				{
					throw new IllegalStateException();
				}
				timestamp = DATE_BEFORE_RENAMES;
			}
			else
			{
				timestamp = Instant.ofEpochMilli(Long.parseLong(tso.toString()));
			}
			history.put(timestamp, name);
		}
		return history;
	}

	/**
	 * Eventually tells the <code>enthusiast</code> about <code>uuid</code>'s name history.
	 * Fetches info asynchronously and informs info synchronously ASAP.
	 * @param uuid The {@link java.util.UUID} of the player whose name history is desired.
	 * @param enthusiast The {@link IHistoryEnthusiast} for which {@link IHistoryEnthusiast#learn(UUID, SortedMap)} will be called.
	 */
	public void tellHistory(final UUID uuid, final IHistoryEnthusiast enthusiast)
	{
		final String uuid_str = uuid.toString().replaceAll("[^a-zA-Z0-9]", "");
		cacheFolder.mkdirs();
		cacheFolder.mkdir();
		final File cacheFile = new File(cacheFolder, uuid_str);
		if(cacheFile.exists())
		{
			try
			{
				SortedMap<Instant, String> history = checkCache(cacheFile);
				if(history.get(history.lastKey()).equalsIgnoreCase(inst.getServer().getOfflinePlayer(uuid).getName()))
				{
					enthusiast.learn(uuid, history);
					return;
				}
				else throw new IllegalStateException();
			}
			catch(IOException|InvalidConfigurationException|NullPointerException|IllegalStateException|NumberFormatException e)
			{
				StringWriter w = new StringWriter();
				e.printStackTrace(new PrintWriter(w));
				inst.tellConsole("Deleting cache file "+uuid_str+" due to this exception:\n"+w);
				cacheFile.delete();
			}
		}
		enthusiast.holdBreath();
		inst.getServer().getScheduler().runTaskAsynchronously(inst, new Runnable(){@Override public void run()
		{
			try
			{
				YamlConfiguration json = new YamlConfiguration();
				json.load(new InputStreamReader(new URL("https://api.mojang.com/user/profiles/"+uuid_str+"/names").openStream()));
				json.save(cacheFile);
				final SortedMap<Instant, String> history = checkCache(cacheFile);
				inst.getServer().getScheduler().runTask(inst, new Runnable(){@Override public void run()
				{
					enthusiast.learn(uuid, history);
				}});
			}
			catch(IOException|InvalidConfigurationException|NullPointerException|IllegalStateException|NumberFormatException e)
			{
				inst.getServer().getScheduler().runTask(inst, new Runnable(){@Override public void run()
				{
					enthusiast.letDown(e);
				}});
			}
		}});
	}

	/**
	 * Looks up the present identity of <code>name</code> from <code>date</code> and then calls {@link #tellHistory(UUID, IHistoryEnthusiast)}
	 * @param name The name of the player as it was on <code>date</code>.
	 * @param date The date when the player had the <code>name</code>.
	 * @param enthusiast Will be passed on.
	 */
	public void tellHistory(final String name, final Instant date, final IHistoryEnthusiast enthusiast)
	{
		if(name.length() > 16 || !name.matches("\\w+"))
		{
			enthusiast.letDown(new IllegalArgumentException("\""+name+"\" is not a valid Minecraft username"));
		}
		enthusiast.holdBreath();
		inst.getServer().getScheduler().runTaskAsynchronously(inst, new Runnable(){@Override public void run()
		{
			try
			{
				YamlConfiguration json = new YamlConfiguration();
				json.load(new InputStreamReader(new URL("https://api.mojang.com/user/profiles/minecraft/"+name+"?at="+(date.toEpochMilli()/1000L)).openStream()));
				final UUID uuid = UUID.fromString(json.get("id").toString().replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
				inst.getServer().getScheduler().runTask(inst, new Runnable(){@Override public void run()
				{
					tellHistory(uuid, enthusiast);
				}});
			}
			catch(IOException|InvalidConfigurationException|NullPointerException e)
			{
				inst.getServer().getScheduler().runTask(inst, new Runnable(){@Override public void run()
				{
					enthusiast.letDown(e);
				}});
			}
		}});
	}
}
