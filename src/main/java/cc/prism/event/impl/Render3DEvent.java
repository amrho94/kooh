package cc.prism.event.impl;

import cc.prism.event.Event;
import com.mojang.blaze3d.vertex.PoseStack;

public class Render3DEvent extends Event {
    private final PoseStack matrices;
    private final float tickDelta;

    public Render3DEvent(PoseStack matrices, float tickDelta) {
        this.matrices = matrices;
        this.tickDelta = tickDelta;
    }

    public PoseStack getMatrices() { return matrices; }
    public float getTickDelta() { return tickDelta; }
}






