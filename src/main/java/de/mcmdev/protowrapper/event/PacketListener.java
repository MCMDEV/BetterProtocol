package de.mcmdev.protowrapper.event;

/**
 * Specification for listeners that should be implemented if you want to listen to packet events.
 * The order in which listeners are processed is equal to their order in the listener list.
 */
public interface PacketListener {

	/**
	 * This method is called when the server receives a packet from the client.
	 *
	 * @param event The event
	 */
	void onReceive(PacketEvent event);

	/**
	 * This method is called when a client receives a packet from the server.
	 *
	 * @param event The event
	 */
	void onSend(PacketEvent event);

}
