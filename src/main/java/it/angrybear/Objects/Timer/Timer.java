package it.angrybear.Objects.Timer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class Timer {
    private Double duration;
    private double counter;
    private Runnable action;
    private final List<TimerIntermediateAction> intermediateActions;
    private Consumer<Double> secondIntermediateAction;
    private BukkitTask task;
    private boolean paused;
    private double interval;

    public Timer(Runnable action, TimerIntermediateAction... intermediateActions) {
        this.action = action;
        this.duration = null;
        this.counter = 0;
        this.intermediateActions = Arrays.asList(intermediateActions);
        this.interval = 1;
    }

    public Timer(JavaPlugin plugin, double duration, Runnable action, TimerIntermediateAction... intermediateActions) {
        this.duration = duration;
        this.action = action;
        this.counter = 0;
        this.intermediateActions = Arrays.asList(intermediateActions);
        this.interval = 1;
        start(plugin, duration);
    }

    public Timer(JavaPlugin plugin, double duration, boolean async, Runnable action, TimerIntermediateAction... intermediateActions) {
        this.duration = duration;
        this.action = action;
        this.counter = 0;
        this.intermediateActions = Arrays.asList(intermediateActions);
        this.interval = 1;
        start(plugin, duration, async);
    }

    public void start(JavaPlugin plugin, double duration) {
        start(plugin, duration, true);
    }

    public void start(JavaPlugin plugin, double duration, boolean async) {
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
        this.task = async ?
                Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, counterRunnable, 0, (long) (interval * 20)) :
                Bukkit.getScheduler().runTaskTimer(plugin, counterRunnable, 0, (long) (interval * 20));
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        counter = 0;
    }

    public boolean isStopped() {
        return task == null ||
                (!Bukkit.getScheduler().isQueued(task.getTaskId()) &&
                !Bukkit.getScheduler().isCurrentlyRunning(task.getTaskId()));
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