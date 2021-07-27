package de.mcmdev.betterprotocol.common.protocol;

import com.github.steveice10.packetlib.packet.BufferedPacket;
import com.github.steveice10.packetlib.packet.DefaultPacketHeader;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.packet.PacketHeader;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.lang.reflect.Constructor;

/**
 * A registry that maps packet id's to their respective {@link Packet} classes Implementations are
 * dependent on a version of MCProtocolLib
 *
 * <p>Implementations for all versions are in common to support multi-version platforms like
 * BungeeCord
 */
public abstract class AbstractProtocolRegistry {

    private final BiMap<Integer, Class<? extends Packet>> incoming = HashBiMap.create();
    private final BiMap<Integer, Class<? extends Packet>> outgoing = HashBiMap.create();

    private final PacketHeader packetHeader = new DefaultPacketHeader();

    public PacketHeader getPacketHeader() {
        return packetHeader;
    }

    public final void registerIncoming(int id, Class<? extends Packet> packet) {
        this.incoming.put(id, packet);
    }

    public final void registerOutgoing(int id, Class<? extends Packet> packet) {
        this.outgoing.put(id, packet);
    }

    public final Packet createIncomingPacket(int id) {
        Class<? extends Packet> packet = this.incoming.get(id);
        if (packet == null) {
            throw new IllegalArgumentException("Invalid packet id: " + id);
        } else {
            try {
                Constructor<? extends Packet> constructor = packet.getDeclaredConstructor();
                if (!constructor.isAccessible()) {
                    constructor.setAccessible(true);
                }

                return constructor.newInstance();
            } catch (NoSuchMethodError var4) {
                throw new IllegalStateException(
                        "Packet \""
                                + id
                                + ", "
                                + packet.getName()
                                + "\" does not have a no-params constructor for instantiation.");
            } catch (Exception var5) {
                throw new IllegalStateException(
                        "Failed to instantiate packet \"" + id + ", " + packet.getName() + "\".",
                        var5);
            }
        }
    }

    public final int getIncomingId(Class<? extends Packet> packetClass) {
        Integer packetId = this.incoming.inverse().get(packetClass);
        if (packetId == null) {
            throw new IllegalArgumentException(
                    "Unregistered outgoing packet class: " + packetClass.getName());
        } else {
            return packetId;
        }
    }

    public final int getIncomingId(Packet packet) {
        return packet instanceof BufferedPacket
                ? this.getIncomingId(((BufferedPacket) packet).getPacketClass())
                : this.getIncomingId(packet.getClass());
    }

    public final int getOutgoingId(Class<? extends Packet> packetClass) {
        Integer packetId = this.outgoing.inverse().get(packetClass);
        if (packetId == null) {
            throw new IllegalArgumentException(
                    "Unregistered outgoing packet class: " + packetClass.getName());
        } else {
            return packetId;
        }
    }

    public final int getOutgoingId(Packet packet) {
        return packet instanceof BufferedPacket
                ? this.getIncomingId(((BufferedPacket) packet).getPacketClass())
                : this.getOutgoingId(packet.getClass());
    }

    public final Packet createOutgoingPacket(int id) {
        Class<? extends Packet> packet = this.outgoing.get(id);
        if (packet == null) {
            throw new IllegalArgumentException("Invalid packet id: " + id);
        } else {
            try {
                Constructor<? extends Packet> constructor = packet.getDeclaredConstructor();
                if (!constructor.isAccessible()) {
                    constructor.setAccessible(true);
                }

                return constructor.newInstance();
            } catch (NoSuchMethodError var4) {
                throw new IllegalStateException(
                        "Packet \""
                                + id
                                + ", "
                                + packet.getName()
                                + "\" does not have a no-params constructor for instantiation.");
            } catch (Exception var5) {
                throw new IllegalStateException(
                        "Failed to instantiate packet \"" + id + ", " + packet.getName() + "\".",
                        var5);
            }
        }
    }
}
