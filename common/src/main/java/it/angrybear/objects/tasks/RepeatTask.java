package it.angrybear.objects.tasks;

import it.angrybear.interfaces.IBearPlugin;
import lombok.Getter;

//TODO: DOc
@Getter
public class RepeatTask extends Task {
    protected int iterations = 0;

    public RepeatTask(Runnable action, TimerIntermediateAction... intermediateActions) {
        super(action, intermediateActions);
    }

    public RepeatTask(IBearPlugin plugin, Runnable action, TimerIntermediateAction... intermediateActions) {
        super(plugin, action, intermediateActions);
    }

    public RepeatTask(IBearPlugin plugin, double duration, Runnable action, TimerIntermediateAction... intermediateActions) {
        super(plugin, duration, action, intermediateActions);
    }

    public RepeatTask(IBearPlugin plugin, double duration, boolean async, Runnable action, TimerIntermediateAction... intermediateActions) {
        super(plugin, duration, async, action, intermediateActions);
    }

    @Override
    protected Runnable createCounterRunnable() {
        return () -> {
            if (paused) return;
            if (counter >= duration * (iterations + 1)) {
                action.run();
                iterations++;
            }
            getIntermediateAction(counter).ifPresent(t -> t.runAction(counter));
            if (secondIntermediateAction != null) secondIntermediateAction.accept(counter);
            counter += interval;
        };
    }
}
