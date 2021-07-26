package de.mcmdev.betterprotocol;

import de.mcmdev.betterprotocol.api.BetterProtocolAPI;

public class BetterProtocol {

    private static BetterProtocolAPI INSTANCE;

    public static void provide(BetterProtocolAPI INSTANCE) {
        if(BetterProtocol.INSTANCE != null)    {
            throw new IllegalStateException("An instance has already been provided.");
        }
        BetterProtocol.INSTANCE = INSTANCE;
    }

    public static <P> BetterProtocolAPI<P> get() {
        if(INSTANCE == null)    {
            throw new IllegalStateException("No BetterProtocol instance provided. Please check if your platform is supported.");
        }
        return INSTANCE;
    }
}
