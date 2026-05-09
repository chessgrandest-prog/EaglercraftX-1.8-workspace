package net.minecraft.client.module;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private static final List<Module> modules = new ArrayList<>();

    static {
        // Combat
        modules.add(new KillAuraModule());
        modules.add(new AimBotModule());
        modules.add(new TriggerBotModule());
        modules.add(new AutoArmorModule());
        modules.add(new CriticalsModule());
        modules.add(new ReachModule());
        modules.add(new VelocityModule());
        modules.add(new AutoClickerModule());
        modules.add(new BowAimbotModule());

        // Movement
        modules.add(new AutoSprintModule());
        modules.add(new NoFallModule());
        modules.add(new BHopModule());
        modules.add(new FlyModule());
        modules.add(new AirJumpModule());
        modules.add(new InventoryMoveModule());
        modules.add(new SpeedModule());
        modules.add(new JesusModule());
        modules.add(new SpiderModule());
        modules.add(new StepModule());
        modules.add(new SafeWalkModule());
        modules.add(new NoSlowDownModule());
        modules.add(new PhaseModule());
        modules.add(new BlinkModule());
        modules.add(new ParkourModule());

        // Render
        modules.add(new ESPModule());
        modules.add(new TracersModule());
        modules.add(new MinimapModule());
        modules.add(new FullbrightModule());
        modules.add(new XRayModule());
        modules.add(new NameTagsModule());
        modules.add(new ChestESPModule());
        modules.add(new OreESPModule());
        modules.add(new FreecamModule());

        // Misc
        modules.add(new AutoRespawnModule());
        modules.add(new FastPlaceModule());
        modules.add(new ChestStealerModule());
        modules.add(new AutoToolModule());
        modules.add(new AutoMineModule());
        modules.add(new FastBreakModule());
        modules.add(new NukerModule());
        modules.add(new ScaffoldModule());
        modules.add(new AutoEatModule());
        modules.add(new AutoFarmModule());
        modules.add(new AutoFishModule());
    }

    public static List<Module> getModules() { return modules; }

    public static Module getModule(String name) {
        return modules.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}