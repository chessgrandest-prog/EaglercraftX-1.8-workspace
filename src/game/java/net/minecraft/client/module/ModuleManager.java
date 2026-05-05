package net.minecraft.client.module;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private static final List<Module> modules = new ArrayList<>();

    static {
        // Example module – you'll add more later
            modules.add(new AutoSprintModule());
            modules.add(new FullbrightModule());
            modules.add(new NoFallModule());
            modules.add(new AutoRespawnModule());
            modules.add(new FastPlaceModule());
            modules.add(new KillAuraModule());
            modules.add(new ChestStealerModule());
            modules.add(new AutoToolModule());
            modules.add(new ESPModule());
            modules.add(new TracersModule());
            modules.add(new MinimapModule());
            modules.add(new InventoryMoveModule());

            modules.add(new AutoArmorModule());
            modules.add(new AutoMineModule());
            modules.add(new BHopModule());
            modules.add(new FlyModule());
            modules.add(new AirJumpModule());
            modules.add(new AimBotModule());
            modules.add(new TriggerBotModule());
    }

    public static List<Module> getModules() { return modules; }

    public static Module getModule(String name) {
        return modules.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}