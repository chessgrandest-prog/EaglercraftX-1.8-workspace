package net.minecraft.client.module;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.Packet;

public class BlinkModule extends Module {
    private final List<Packet> packetQueue = new ArrayList<>();

    public BlinkModule() {
        super("Blink", "Movement");
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer != null && mc.thePlayer.sendQueue != null) {
            for (Packet packet : packetQueue) {
                mc.thePlayer.sendQueue.getNetworkManager().sendPacket(packet);
            }
        }
        packetQueue.clear();
    }

    @Override
    public void onEnable() {
        packetQueue.clear();
    }

    public List<Packet> getPacketQueue() {
        return packetQueue;
    }
}
