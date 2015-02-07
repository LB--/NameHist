package com.lb_stuff.namehist.api;

import java.time.Instant;
import java.util.SortedMap;
import java.util.UUID;

/**
 * Implement this interface to call {@link NameHistService#tellHistory(UUID, IHistoryEnthusiast)}
 */
public interface IHistoryEnthusiast
{
	/**
	 * Called when the cache lookup fails and the name history has to be downloaded.
	 */
	void holdBreath();
	/**
	 * Called synchronously when name history is available.
	 * @param uuid The UUID of the player whose name history was requested.
	 * @param history The actual name history.
	 */
	void learn(UUID uuid, SortedMap<Instant, String> history);
	/**
	 * Called if name history cannot be retrieved.
	 * @param t The exception that occurred.
	 */
	void letDown(Throwable t);
}
