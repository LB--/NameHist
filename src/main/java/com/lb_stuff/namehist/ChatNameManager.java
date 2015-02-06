package com.lb_stuff.namehist;

public final class ChatNameManager implements OptionalComponent
{
	private final NameHistPlugin inst;
	private OptionalComponent chatnamelistener = null;
	public ChatNameManager(NameHistPlugin plugin)
	{
		inst = plugin;
		if(hasEventFilterServices())
		{
			chatnamelistener = new ChatNameListener(inst);
		}
	}

	private Boolean hasefs = null;
	public boolean hasEventFilterServices()
	{
		if(hasefs == null)
		{
			try
			{
				Class.forName("com.lb_stuff.eventfilterservices.EventFilterServices");
				Class.forName("com.lb_stuff.eventfilterservices.ChatFilterService");
				hasefs = true;
			}
			catch(ClassNotFoundException e)
			{
				hasefs = false;
			}
		}
		return hasefs;
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
		if(chatnamelistener != null)
		{
			chatnamelistener.start();
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
		if(chatnamelistener != null)
		{
			chatnamelistener.stop();
		}
		started = false;
	}
}
