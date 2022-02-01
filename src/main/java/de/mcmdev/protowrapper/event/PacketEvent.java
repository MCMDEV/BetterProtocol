package de.mcmdev.protowrapper.event;

import com.github.steveice10.packetlib.packet.Packet;
import org.bukkit.entity.Player;

public class PacketEvent {

	private final Player player;
	private Packet packet;
	private boolean cancelled;
	private boolean dirty;

	public PacketEvent(Player player, Packet packet) {
		this.player = player;
		this.packet = packet;
		this.cancelled = false;
		this.dirty = false;
	}

	/**
	 * Gets the player that the packet of this event is
	 * being sent to/the packet of this event is
	 * being received from.
	 *
	 * @return The involved player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Whether the packet should be processed further.
	 */
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Whether the packet should be processed further.
	 *
	 * @param cancelled If true, processing will not occur
	 */
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	/**
	 * Gets the packet that is being sent/received.
	 *
	 * @return The packet
	 */
	public Packet getPacket() {
		return packet;
	}

	/**
	 * Sets a new packet that will be processed instead of the old one.
	 * Note that this may not be the final packet if this method is not called by the
	 * last packet listener in the list.
	 *
	 * @param packet The new packet
	 */
	public void setPacket(Packet packet) {
		this.packet = packet;
		this.dirty = true;
	}

	/**
	 * Whether the packet has been modified. If this is false, the channel handlers will pass the original packet
	 * instead of the modified one to prevent any possible issues with the packet wrappers.
	 *
	 * @return True, if the packet has been modified
	 */
	public boolean isDirty() {
		return dirty;
	}
}
