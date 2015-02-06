package com.lb_stuff.namehist.api;

import com.lb_stuff.namehist.NameHistPlugin;

import java.io.File;

public class NameHistService
{
	private final NameHistPlugin inst;
	private final File cacheFile;
	public NameHistService(NameHistPlugin plugin)
	{
		inst = plugin;
		cacheFile = new File(inst.getDataFolder(), "cache.yml");
	}

	//TODO
}
