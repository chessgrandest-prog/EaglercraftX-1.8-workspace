package net.minecraft.client.module;

public class NoSlowDownModule extends Module {
    public NoSlowDownModule() { super("NoSlowDown", "Movement"); }
    // NoSlowDown cancels the slowdown from using items (eating, blocking, etc.)
    // This acts as a flag checked in EntityPlayerSP.onLivingUpdate where item slowdown is applied.
}
