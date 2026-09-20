package cc.prism.module;

import cc.prism.module.impl.client.InterfaceModule;
import cc.prism.module.impl.combat.KillAura;
import cc.prism.module.impl.combat.Velocity;
import cc.prism.module.impl.movement.*;
import cc.prism.module.impl.player.*;
import cc.prism.module.impl.visual.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        // Combat
        register(new KillAura());
        register(new Velocity());

        // Movement
        register(new Sprint());
        register(new Flight());
        register(new Speed());
        register(new NoFall());
        register(new NoSlow());
        register(new NoJumpDelay());

        // Player
        register(new AutoTool());
        register(new FastPlace());
        register(new NoPlaceDelay());

        // Visual
        register(new ESP());
        register(new FullBright());
        register(new Tracers());
        register(new ClickGuiModule());
        register(new HudModule());

        // Client
        register(new InterfaceModule());
    }

    private void register(Module module) {
        modules.add(module);
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Module> getByCategory(Category category) {
        return modules.stream()
                .filter(m -> m.getCategory() == category)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T get(Class<T> clazz) {
        return (T) modules.stream()
                .filter(m -> m.getClass() == clazz)
                .findFirst()
                .orElse(null);
    }

    /** Returns enabled, non-hidden modules sorted A→Z for ArrayList rendering. */
    public List<Module> getEnabledSorted() {
        return modules.stream()
                .filter(Module::isEnabled)
                .filter(m -> !m.isHidden())
                .sorted(Comparator.comparing(Module::getName))
                .collect(Collectors.toList());
    }

    public void onKeyPress(int keyCode) {
        for (Module m : modules) {
            if (m.getKeybind() == keyCode) m.toggle();
        }
    }
}
