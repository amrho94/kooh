package cc.prism.util;

public class TimerUtil {
    private long lastMs = System.currentTimeMillis();

    public boolean hasReached(double ms) {
        return System.currentTimeMillis() - lastMs >= ms;
    }

    public void reset() {
        lastMs = System.currentTimeMillis();
    }

    public long getElapsed() {
        return System.currentTimeMillis() - lastMs;
    }
}
