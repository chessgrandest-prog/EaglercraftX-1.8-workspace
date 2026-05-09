package net.minecraft.client.module;

public class FastBreakModule extends Module {
    public FastBreakModule() { super("FastBreak", "Misc"); }
    // FastBreak speeds up block breaking. This acts as a flag that should be
    // checked in PlayerControllerMP.onPlayerDamageBlock to multiply the damage increment.
    // A hook in PlayerControllerMP is needed for full functionality.
}
