package cc.prism.module.impl.combat;

import cc.prism.event.EventTarget;
import cc.prism.event.impl.TickEvent;
import cc.prism.module.Category;
import cc.prism.module.Module;
import cc.prism.property.BooleanProperty;
import cc.prism.property.ModeProperty;
import cc.prism.property.NumberProperty;
import cc.prism.util.RotationUtil;
import cc.prism.util.TimerUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * KillAura â€” automatically attacks valid nearby entities.
 *
 * Properties:
 *   Target       â€“ Players / Mobs / All
 *   Range        â€“ maximum attack distance (blocks)
 *   CPS          â€“ clicks (attacks) per second
 *   Rotate       â€“ silently snap rotations to the target
 *   Through Walls â€“ attack through solid blocks (no visibility check)
 *   W-Tap        â€“ briefly cancel sprint before each attack to boost crit/
 *                  damage-reset mechanics then re-enable sprint afterward
 */
public class KillAura extends Module {

    private final ModeProperty   targetMode   = addProperty(new ModeProperty  ("Target",        "Players", "Players", "Mobs", "All"));
    private final NumberProperty range        = addProperty(new NumberProperty ("Range",         3.5,  1.0, 6.0, 0.1));
    private final NumberProperty cps          = addProperty(new NumberProperty ("CPS",           12,   1,  20,  1));
    private final BooleanProperty rotate      = addProperty(new BooleanProperty("Rotate",        true));
    private final BooleanProperty throughWalls= addProperty(new BooleanProperty("Through Walls", false));
    private final BooleanProperty wTap        = addProperty(new BooleanProperty("W-Tap",         false));

    private final TimerUtil attackTimer = new TimerUtil();

    public KillAura() {
        super("KillAura", "Automatically attacks nearby entities", Category.COMBAT);
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) return;

        // Throttle attacks to the configured CPS.
        if (!attackTimer.hasReached(1000.0 / cps.getInt())) return;

        // â”€â”€ Build target list â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        List<Entity> targets = new ArrayList<>();
        for (Entity Entity : mc.world.getEntities()) {
            if (!isValidTarget(Entity)) continue;
            if (mc.player.distanceTo(Entity) > range.getFloat()) continue;

            // Visibility check â€” skip if Through Walls is off and no clear LOS.
            if (!throughWalls.getValue()) {
                if (mc.world.raycastBlock(
                        mc.player.getEyePos(),
                        Entity.position().add(0, Entity.getHeight() / 2.0, 0),
                        net.minecraft.block.ShapeContext.absent()) != null) {
                    continue;
                }
            }

            targets.add(Entity);
        }

        if (targets.isEmpty()) return;

        // Sort by distance; attack the closest.
        targets.sort(Comparator.comparingDouble(mc.player::distanceTo));
        Entity target = targets.get(0);

        // â”€â”€ Rotate silently toward target â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        if (rotate.getValue()) {
            float[] rots = RotationUtil.getRotationsToEntity(target);
            mc.player.setYRot(rots[0]);
            mc.player.setXRot(rots[1]);
        }

        // â”€â”€ W-Tap: cancel sprint â†’ attack â†’ re-enable sprint â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        if (wTap.getValue()) {
            mc.player.setSprinting(false);
        }

        // â”€â”€ Perform attack â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        mc.gameMode.attack(mc.player, target);
        mc.player.swing(InteractionHand.MAIN_HAND);
        attackTimer.reset();

        // Re-enable sprint after attack if W-Tap is active.
        if (wTap.getValue()) {
            mc.player.setSprinting(true);
        }
    }

    // â”€â”€ Target validation â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private boolean isValidTarget(Entity Entity) {
        if (Entity == mc.player)                       return false;
        if (!(Entity instanceof LivingEntity living))  return false;
        if (living.isDeadOrDying())                           return false;

        return switch (targetMode.getValue()) {
            case "Players" -> Entity instanceof Player;
            case "Mobs"    -> !(Entity instanceof Player);
            case "All"     -> true;
            default        -> false;
        };
    }
}






