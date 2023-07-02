package it.angrybear.Objects.Timer;

import java.util.function.Consumer;

public class TimerIntermediateAction {
    private double time;
    private Consumer<Double> action;

    public TimerIntermediateAction(double time, Consumer<Double> action) {
        this.time = time;
        this.action = action;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public Consumer<Double> getAction() {
        return action;
    }

    public void setAction(Consumer<Double> action) {
        this.action = action;
    }

    public void runAction(double timer) {
        if (this.action != null) this.action.accept(timer);
    }
}