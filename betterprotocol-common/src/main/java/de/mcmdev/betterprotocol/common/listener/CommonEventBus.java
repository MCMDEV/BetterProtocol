package de.mcmdev.betterprotocol.common.listener;

import com.github.steveice10.packetlib.packet.Packet;
import com.nesaak.noreflection.NoReflection;
import com.nesaak.noreflection.access.DynamicCaller;
import de.mcmdev.betterprotocol.api.EventBus;
import de.mcmdev.betterprotocol.api.PacketEvent;
import de.mcmdev.betterprotocol.api.PacketHandler;
import de.mcmdev.betterprotocol.api.PacketListener;
import de.mcmdev.betterprotocol.api.PacketListenerFunction;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds all registered {@link PacketListenerFunction}'s and fires them
 *
 * @param <P> The player type
 */
public class CommonEventBus<P> implements EventBus<P> {

    private final Map<Class<? extends Packet>, List<PacketListenerFunction>> listenerMap = new HashMap<>();

    public <T extends Packet> void listen(Class<T> packetClass, PacketListenerFunction<T, P> packetListenerFunction) {
        listenerMap.putIfAbsent(packetClass, new ArrayList<>());
        listenerMap.get(packetClass).add(packetListenerFunction);
    }

    @Override
    public void listen(PacketListener listener) {
        // traverse the class hierarchy upwards
        // to find annotated methods across all superclasses
        Class<?> clazz = listener.getClass();
        while (!clazz.equals(Object.class)) {
            for (Method method : clazz.getDeclaredMethods()) {
                // check the presence of the annotation
                PacketHandler packetHandlerAnnotation = method.getAnnotation(PacketHandler.class);
                if (packetHandlerAnnotation == null) {
                    continue;
                }

                // check the method signature
                if (method.getReturnType() != void.class) {
                    throw new IllegalArgumentException("Methods annotated with @PacketHandler must return void");
                }

                if (method.getParameterCount() != 1
                        || !PacketEvent.class.isAssignableFrom(method.getParameterTypes()[0])) {
                    throw new IllegalArgumentException("Methods annotated with @PacketHandler must have a PacketEvent as the only argument");
                }

                // determine the packet event's packet type (first generic type argument)
                if (!(method.getGenericParameterTypes()[0] instanceof ParameterizedType)) {
                    throw new IllegalArgumentException("Could not determine the PacketEvent's Packet generic parameter - is it defined?");
                }

                Type packetType = ((ParameterizedType) method.getGenericParameterTypes()[0]).getActualTypeArguments()[0];
                if (!(packetType instanceof Class)) {
                    throw new IllegalArgumentException("Could not determine the PacketEvent's Packet generic parameter's class");
                }

                Class<? extends Packet> packetClass = (Class<? extends Packet>) packetType;

                // the annotation is present and the signature is correct -
                // register the method as a listener using a lambda
                // instead of reflection for invocation to increase performance
                DynamicCaller noReflectionMethod = NoReflection.shared().get(method);

                listen(packetClass, event -> {
                    noReflectionMethod.call(listener, event);
                });
            }

            clazz = clazz.getSuperclass();
        }
    }

    public void post(PacketEvent<?, P> event) {
        List<PacketListenerFunction> packetListenerFunctions = listenerMap.get(event.getPacketType());
        if (packetListenerFunctions == null) {
            return;
        }
        packetListenerFunctions.forEach(packetListenerFunction -> packetListenerFunction.handle(event));
    }
}
