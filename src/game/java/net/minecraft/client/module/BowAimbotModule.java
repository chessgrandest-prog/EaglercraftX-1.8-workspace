package net.minecraft.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBow;
import net.minecraft.util.MathHelper;

public class BowAimbotModule extends Module {
    private static final double GRAVITY_PER_TICK = 0.05;

    public BowAimbotModule() { super("BowAimbot", "Combat"); }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (mc.thePlayer.getCurrentEquippedItem() == null) return;
        if (!(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow)) return;
        if (!mc.thePlayer.isUsingItem()) return;

        Entity target = findNearest(mc, 64.0);
        if (target == null) return;

        double aimY = target.posY + (double) target.height * 0.55D;
        double dx = target.posX - mc.thePlayer.posX;
        double dy = aimY - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
        double dz = target.posZ - mc.thePlayer.posZ;
        double dist = MathHelper.sqrt_double(dx * dx + dz * dz);

        int charge = mc.thePlayer.getItemInUseDuration();
        float velocity = Math.min(charge / 20.0F, 1.0F);
        velocity = (velocity * velocity + velocity * 2.0F) / 3.0F;
        if (velocity > 1.0F) velocity = 1.0F;
        double arrowSpeed = Math.max(0.1D, velocity * 3.0D);

        double time = dist / arrowSpeed;
        double drop = GRAVITY_PER_TICK * time * time;

        float yaw = (float)(Math.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;
        float pitch = (float)(-(Math.atan2(dy + drop, dist) * (180.0 / Math.PI)));

        mc.thePlayer.rotationYaw = yaw;
        mc.thePlayer.rotationPitch = pitch;
    }

    private Entity findNearest(Minecraft mc, double range) {
        Entity nearest = null;
        double nearestDist = range;
        for (Object obj : mc.theWorld.loadedEntityList) {
            Entity e = (Entity) obj;
            if (e == mc.thePlayer) continue;
            if (!(e instanceof EntityLivingBase)) continue;
            if (!e.isEntityAlive()) continue;
            double d = mc.thePlayer.getDistanceToEntity(e);
            if (d < nearestDist) {
                nearestDist = d;
                nearest = e;
            }
        }
        return nearest;
    }
}
