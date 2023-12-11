package it.angrybear.objects.tasks;

import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

/**
 * A wrapper for an action that should be
 * executed at a certain time interval.
 */
@Getter
@Setter
public class TimerIntermediateAction {
    private double time;
    private Consumer<Double> action;

    public TimerIntermediateAction(double time, Consumer<Double> action) {
        this.time = time;
        this.action = action;
    }

    /**
     * Run the action at the given time.
     *
     * @param time the time
     */
    public void runAction(double time) {
        if (this.action != null) this.action.accept(time);
    }
}