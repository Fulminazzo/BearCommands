package it.angrybear.Objects.Timer;

import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Utils.ServerUtils;
import it.fulminazzo.reflectionutils.Objects.ReflObject;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Timer {
    private Double duration;
    private double counter;
    private Runnable action;
    private final List<TimerIntermediateAction> intermediateActions;
    private Consumer<Double> secondIntermediateAction;
    private ReflObject<?> task;
    private boolean paused;
    private double interval;

    public Timer(Runnable action, TimerIntermediateAction... intermediateActions) {
        this.action = action;
        this.duration = null;
        this.counter = 0;
        this.intermediateActions = new ArrayList<>();
        this.intermediateActions.addAll(Arrays.asList(intermediateActions));
        this.interval = 1;
    }

    public Timer(IBearPlugin<?> plugin, double duration, Runnable action, TimerIntermediateAction... intermediateActions) {
        this.duration = duration;
        this.action = action;
        this.counter = 0;
        this.intermediateActions = new ArrayList<>();
        this.intermediateActions.addAll(Arrays.asList(intermediateActions));
        this.interval = 1;
        start(plugin, duration);
    }

    public Timer(IBearPlugin<?> plugin, double duration, boolean async, Runnable action, TimerIntermediateAction... intermediateActions) {
        this.duration = duration;
        this.action = action;
        this.counter = 0;
        this.intermediateActions = new ArrayList<>();
        this.intermediateActions.addAll(Arrays.asList(intermediateActions));
        this.interval = 1;
        start(plugin, duration, async);
    }

    public void start(IBearPlugin<?> plugin, double duration) {
        start(plugin, duration, true);
    }

    public void start(IBearPlugin<?> plugin, double duration, boolean async) {
        if (task != null) return;
        this.duration = duration;
        Runnable counterRunnable = () -> {
            if (paused) return;
            if (counter >= duration) {
                action.run();
                if (duration >= 0) stop();
                return;
            }
            getIntermediateAction(counter).ifPresent(t -> t.runAction(counter));
            if (secondIntermediateAction != null) secondIntermediateAction.accept(counter);
            counter += interval;
        };
        if (ServerUtils.isBukkit())
            this.task = ServerUtils.getScheduler().callMethod(async ? "runTaskTimerAsynchronously" : "runTaskTimer",
                    plugin, counterRunnable, 0L, (long) (interval * 20));
        else this.task = ServerUtils.getScheduler().callMethod("schedule", plugin, counterRunnable,
                0L, (long) interval, TimeUnit.SECONDS);
    }

    public void stop() {
        if (task != null) {
            task.callMethod("cancel");
            task = null;
        }
        counter = 0;
    }

    public boolean isStopped() {
        if (task == null) return true;
        if (ServerUtils.isBukkit()) {
            ReflObject<?> scheduler = ServerUtils.getScheduler();
            boolean queued = scheduler.getMethodObject("isQueued", getId());
            boolean running = scheduler.getMethodObject("isCurrentlyRunning", getId());
            return !queued && !running;
        } else return false;
    }

    public int getId() {
        return task == null ? -1 : task.getMethodObject(ServerUtils.isBukkit() ? "getTaskId" : "getId");
    }

    public void resume() {
        this.paused = false;
    }

    public void pause() {
        this.paused = true;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setDuration(Double duration) {
        if (duration < 0) throw new IllegalArgumentException("Duration cannot be negative!");
        this.duration = duration;
    }

    public void addIntermediateActions(TimerIntermediateAction... intermediateActions) {
        Arrays.stream(intermediateActions).forEach(i -> {
            removeIntermediateActions(i);
            this.intermediateActions.add(i);
        });
    }

    public void removeIntermediateActions(TimerIntermediateAction... intermediateActions) {
        removeIntermediateActions(Arrays.stream(intermediateActions)
                .map(TimerIntermediateAction::getTime)
                .toArray(Double[]::new));
    }

    public void removeIntermediateActions(Double... intermediates) {
        Arrays.stream(intermediates).filter(Objects::nonNull).forEach(i ->
                this.intermediateActions.removeIf(a -> a.getTime() == i));
    }

    public Optional<TimerIntermediateAction> getIntermediateAction(double intermediate) {
        return this.intermediateActions.stream().filter(a -> a.getTime() == intermediate).findAny();
    }

    public List<TimerIntermediateAction> getIntermediateActions() {
        return intermediateActions;
    }

    public void setSecondIntermediateAction(Consumer<Double> secondIntermediateAction) {
        this.secondIntermediateAction = secondIntermediateAction;
    }

    public Consumer<Double> getSecondIntermediateAction() {
        return secondIntermediateAction;
    }

    public void setInterval(double interval) {
        if (interval < 0.05) interval = 0.05;
        this.interval = interval;
    }

    public double getInterval() {
        return interval;
    }

    public double getCounter() {
        return counter;
    }

    public double getDuration() {
        return duration;
    }

    public Runnable getAction() {
        return action;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }
}