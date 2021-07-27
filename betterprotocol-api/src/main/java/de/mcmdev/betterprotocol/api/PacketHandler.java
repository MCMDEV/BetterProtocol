package de.mcmdev.betterprotocol.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Functions marked with this annotation are registered as packet listeners when an instance of the
 * containing class is passed to {@link EventBus#listen(PacketListener)}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PacketHandler {
    // TODO: priority
}
