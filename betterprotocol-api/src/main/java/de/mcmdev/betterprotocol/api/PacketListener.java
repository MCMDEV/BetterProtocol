package de.mcmdev.betterprotocol.api;

/**
 * Classes implementing this interface can be passed to {@link EventBus#listen(PacketListener)} to
 * register all of its methods annotated with {@link PacketHandler} as packet handlers.
 */
public interface PacketListener {}
