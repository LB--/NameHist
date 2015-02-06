package com.lb_stuff.namehist;

public final class NametagManager implements OptionalComponent
{
	private final NameHistPlugin inst;
	private OptionalComponent nametaglistener = null;
	public NametagManager(NameHistPlugin plugin)
	{
		inst = plugin;
		if(hasTagAPI())
		{
			nametaglistener = new PartyNametagListener(inst);
		}
	}

	private Boolean hastagapi = null;
	public boolean hasTagAPI()
	{
		if(hastagapi == null)
		{
			try
			{
				Class.forName("org.kitteh.tag.TagAPI");
				Class.forName("org.kitteh.tag.AsyncPlayerReceiveNameTagEvent");
				hastagapi = true;
			}
			catch(ClassNotFoundException e)
			{
				hastagapi = false;
			}
		}
		return hastagapi;
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
		if(nametaglistener != null)
		{
			nametaglistener.start();
		}
		started = true;
	}
	@Override
	public void stop()
	{
		if(!isStarted())
		{
			return;
		}
		if(nametaglistener != null)
		{
			nametaglistener.stop();
		}
		started = false;
	}
}
