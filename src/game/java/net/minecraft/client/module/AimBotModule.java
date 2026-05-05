package net.minecraft.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class AimBotModule extends Module {
    public AimBotModule() { super("AimBot", "Combat"); }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;

        Entity target = findNearestTarget(mc);
        if (target == null) return;

        faceEntity(mc, target);
    }

    private Entity findNearestTarget(Minecraft mc) {
        Entity nearest = null;
        double nearestDist = 6.0D;
        for (Object o : mc.theWorld.loadedEntityList) {
            Entity e = (Entity) o;
            if (e == mc.thePlayer) continue;
            if (!(e instanceof EntityLivingBase)) continue;
            if (!e.isEntityAlive()) continue;
            double dist = mc.thePlayer.getDistanceToEntity(e);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = e;
            }
        }
        return nearest;
    }

    private void faceEntity(Minecraft mc, Entity target) {
        double dx = target.posX - mc.thePlayer.posX;
        double dy = (target.posY + target.getEyeHeight()) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double dz = target.posZ - mc.thePlayer.posZ;
        double horizontalDist = MathHelper.sqrt_double(dx * dx + dz * dz);
        float yaw = (float)(Math.atan2(dz, dx) * (180D / Math.PI)) - 90F;
        float pitch = (float)(-(Math.atan2(dy, horizontalDist) * (180D / Math.PI)));
        mc.thePlayer.rotationYaw = yaw;
        mc.thePlayer.rotationPitch = pitch;
    }
}