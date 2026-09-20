package cc.prism.module.impl.player;

import cc.prism.event.EventTarget;
import cc.prism.event.impl.TickEvent;
import cc.prism.module.Category;
import cc.prism.module.Module;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.*;
import net.minecraft.core.BlockPos;

public class AutoTool extends Module {

    public AutoTool() {
        super("AutoTool", "Automatically switches to the best tool", Category.PLAYER);
    }

    @EventTarget
    public void onTick(TickEvent e) {
        if (mc.player == null || mc.world == null) return;
        if (mc.crosshairTarget == null) return;
        if (!(mc.crosshairTarget instanceof net.minecraft.world.phys.BlockHitResult bhr)) return;

        BlockPos pos = bhr.getBlockPos();
        BlockState state = mc.world.getBlockState(pos);
        if (state.isAir()) return;

        Inventory inv = mc.player.getInventory();
        int bestSlot = -1;
        float bestSpeed = -1f;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = inv.getStack(i);
            float speed = stack.getMiningSpeedMultiplier(state);
            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }

        if (bestSlot != -1 && bestSpeed > 1.0f) {
            inv.selectedSlot = bestSlot;
        }
    }
}






