package com.lb_stuff.namehist.api;

import org.bukkit.command.CommandSender;

/**
 * Permission checking for {@link CommandSender}s.
 */
public final class Perms
{
	private Perms()
	{
		throw new UnsupportedOperationException();
	}

	private static boolean P(CommandSender s, String p)
	{
		return s != null && s.hasPermission("NameHist."+p);
	}

	public static boolean pluginInfo    (CommandSender s){ return s != null && s.hasPermission("NameHist"); }
	public static boolean pluginReload  (CommandSender s){ return P(s, "reload"); }
	public static boolean nameHistory   (CommandSender s){ return s != null && s.hasPermission("NameHist"); }
	public static boolean namePreference(CommandSender s){ return P(s, "prefer"); }
	public static boolean updateInform  (CommandSender s){ return P(s, "update-notify"); }
}
