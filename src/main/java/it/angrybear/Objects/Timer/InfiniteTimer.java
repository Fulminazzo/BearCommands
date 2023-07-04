package it.angrybear.Objects.Timer;

import it.angrybear.Interfaces.IBearPlugin;

public class InfiniteTimer extends Timer {
    public InfiniteTimer(Runnable action, TimerIntermediateAction... intermediateActions) {
        super(action, intermediateActions);
    }

    public InfiniteTimer(IBearPlugin<?> plugin, Runnable action, TimerIntermediateAction... intermediateActions) {
        super(plugin, -1, action, intermediateActions);
    }

    public InfiniteTimer(IBearPlugin<?> plugin, boolean async, Runnable action, TimerIntermediateAction... intermediateActions) {
        super(plugin, -1, async, action, intermediateActions);
    }

    @Deprecated
    @Override
    // No point in using duration for an infinite timer.
    public void start(IBearPlugin<?> plugin, double duration) {
        super.start(plugin, duration);
    }

    @Deprecated
    @Override
    // No point in using duration for an infinite timer.
    public void start(IBearPlugin<?> plugin, double duration, boolean async) {
        super.start(plugin, duration, async);
    }

    public void start(IBearPlugin<?> plugin) {
        start(plugin, true);
    }

    public void start(IBearPlugin<?> plugin, boolean async) {
        super.start(plugin, -1, async);
    }
}
