package com.lb_stuff.namehist;

import com.lb_stuff.bukkit.command.PluginReloadCommand;
import com.lb_stuff.namehist.api.IMessenger;
import com.lb_stuff.namehist.api.NameHistService;
import com.lb_stuff.bukkit.config.SmartConfig;
import com.lb_stuff.namehist.command.NameHistCommand;

import net.gravitydevelopment.updater.Updater;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.ChatColor.*;

import java.io.File;
import java.io.IOException;
import java.util.IllegalFormatException;
import java.util.logging.Level;

public final class NameHistPlugin extends JavaPlugin implements IMessenger
{
	private void implementCommand(String name, CommandExecutor command)
	{
		getCommand(name).setExecutor(command);
		if(command instanceof Listener)
		{
			getServer().getPluginManager().registerEvents((Listener)command, this);
		}
	}
	private void implementCommand(String name, TabExecutor command)
	{
		implementCommand(name, (CommandExecutor)command);
		getCommand(name).setTabCompleter(command);
	}

	private final File configFile = new File(getDataFolder(), "config.yml");
	private final ChatNameManager chatnames = new ChatNameManager(this);
	private final NametagManager nametags = new NametagManager(this);
	private SmartConfig config;
	private Updater updater = null;
	@Override
	public void onEnable()
	{
		getServer().getServicesManager().register(NameHistService.class, service, this, ServicePriority.Highest);

		implementCommand("NameHist", new NameHistCommand(this));
		implementCommand("NamePrefer", null); //TODO
		implementCommand("nhreload", new PluginReloadCommand(this));

		boolean firstrun = !configFile.exists();
		try
		{
			getDataFolder().mkdirs();
			config = new SmartConfig(configFile, NameHistPlugin.class);
			config.reload();
		}
		catch(IOException|InvalidConfigurationException e)
		{
			throw new RuntimeException(e);
		}
		reloadSettings(firstrun);
	}
	@Override
	public void onDisable()
	{
	}
	@Override
	public FileConfiguration getConfig()
	{
		return config;
	}
	@Override
	public void reloadConfig()
	{
		try
		{
			config.reload();
		}
		catch(IOException|InvalidConfigurationException e)
		{
			throw new RuntimeException(e);
		}
		if(isEnabled())
		{
			reloadSettings(false);
		}
	}
	@Override
	public void saveDefaultConfig()
	{
		reloadConfig();
	}

	private void reloadSettings(boolean firstrun)
	{
		if(!firstrun)
		{
			if(config.getBoolean("auto-updater"))
			{
				tellConsole("Automatic update downloading is "+GREEN+"enabled"+RESET+" in config");
				if(updater == null)
				{
					updater = new NameHistUpdater(this, getFile(), Updater.UpdateType.DEFAULT);
				}
			}
			else
			{
				tellConsole("Automatic update downloading is "+RED+"disabled"+RESET+" in config");
			}
		}
		else
		{
			tellConsole(YELLOW+"Gravity's Updater may be disabled in the config.");
		}

		if(config.getBoolean("use-tagapi"))
		{
			if(nametags.hasTagAPI())
			{
				tellConsole("Found TagAPI and using it!");
				nametags.start();
			}
			else
			{
				tellConsole(YELLOW+"This plugin requires TagAPI to be installed in order to use the \""+UNDERLINE+"use-tagapi"+RESET+YELLOW+"\" feature (disable in config)");
			}
		}
		else
		{
			nametags.stop();
		}
		if(config.getBoolean("use-eventfilter"))
		{
			if(chatnames.hasEventFilterServices())
			{
				tellConsole("Found EventFilterServices and using it!");
				chatnames.start();
			}
			else
			{
				tellConsole(YELLOW+"This plugin requires EventFilterServices to be installed in order to use the \""+UNDERLINE+"use-eventfilter"+RESET+YELLOW+"\" feature (disable in config)");
			}
		}
		else
		{
			chatnames.stop();
		}
	}

	public static NameHistPlugin getInst()
	{
		return (NameHistPlugin)Bukkit.getServicesManager().getRegistration(NameHistService.class).getPlugin();
	}

	private final NameHistService service = new NameHistService(this);
	public NameHistService getService()
	{
		return service;
	}

	@Override
	public String getMessage(String name, Object... parameters)
	{
		String format = config.getString("messages."+name);
		if(format != null)
		{
			try
			{
				return String.format(format, parameters);
			}
			catch(IllegalFormatException e)
			{
				getLogger().log(Level.WARNING, "Error when using translation \""+name+"\": ", e);
				return "<Broken translation \""+name+"\">";
			}
		}
		getLogger().warning("Missing translation string \""+name+"\"");
		return "<Missing translation \""+name+"\">";
	}
	private static final String PREFIX = ""+AQUA+"[NameHist] "+RESET;
	@Override
	public void tell(CommandSender p, String message)
	{
		for(String line : message.split("\\n"))
		{
			p.sendMessage(PREFIX+line);
		}
	}
	@Override
	public void tellMessage(CommandSender p, String name, Object... parameters)
	{
		tell(p, getMessage(name, parameters));
	}
	@Override
	public void tellConsole(String message)
	{
		tell(getServer().getConsoleSender(), message);
	}
}
