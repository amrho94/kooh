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
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * KillAura — automatically attacks valid nearby entities.
 *
 * Properties:
 *   Target       – Players / Mobs / All
 *   Range        – maximum attack distance (blocks)
 *   CPS          – clicks (attacks) per second
 *   Rotate       – silently snap rotations to the target
 *   Through Walls – attack through solid blocks (no visibility check)
 *   W-Tap        – briefly cancel sprint before each attack to boost crit/
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

        // ── Build target list ────────────────────────────────────────────────
        List<Entity> targets = new ArrayList<>();
        for (Entity entity : mc.world.getEntities()) {
            if (!isValidTarget(entity)) continue;
            if (mc.player.distanceTo(entity) > range.getFloat()) continue;

            // Visibility check — skip if Through Walls is off and no clear LOS.
            if (!throughWalls.getValue()) {
                if (mc.world.raycastBlock(
                        mc.player.getEyePos(),
                        entity.getPos().add(0, entity.getHeight() / 2.0, 0),
                        net.minecraft.block.ShapeContext.absent()) != null) {
                    continue;
                }
            }

            targets.add(entity);
        }

        if (targets.isEmpty()) return;

        // Sort by distance; attack the closest.
        targets.sort(Comparator.comparingDouble(mc.player::distanceTo));
        Entity target = targets.get(0);

        // ── Rotate silently toward target ────────────────────────────────────
        if (rotate.getValue()) {
            float[] rots = RotationUtil.getRotationsToEntity(target);
            mc.player.setYaw(rots[0]);
            mc.player.setPitch(rots[1]);
        }

        // ── W-Tap: cancel sprint → attack → re-enable sprint ────────────────
        if (wTap.getValue()) {
            mc.player.setSprinting(false);
        }

        // ── Perform attack ───────────────────────────────────────────────────
        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);
        attackTimer.reset();

        // Re-enable sprint after attack if W-Tap is active.
        if (wTap.getValue()) {
            mc.player.setSprinting(true);
        }
    }

    // ── Target validation ────────────────────────────────────────────────────

    private boolean isValidTarget(Entity entity) {
        if (entity == mc.player)                       return false;
        if (!(entity instanceof LivingEntity living))  return false;
        if (living.isDead())                           return false;

        return switch (targetMode.getValue()) {
            case "Players" -> entity instanceof PlayerEntity;
            case "Mobs"    -> !(entity instanceof PlayerEntity);
            case "All"     -> true;
            default        -> false;
        };
    }
}
