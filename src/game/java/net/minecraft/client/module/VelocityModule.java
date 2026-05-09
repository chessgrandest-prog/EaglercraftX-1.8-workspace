package net.minecraft.client.module;

public class VelocityModule extends Module {
    public VelocityModule() { super("Velocity", "Combat"); }
    // Velocity cancellation is applied by checking this module in the packet handler.
    // The S12PacketEntityVelocity handler should zero out velocity when this is enabled.
}
