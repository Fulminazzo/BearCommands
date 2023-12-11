package it.angrybear.objects.tasks;

import it.angrybear.exceptions.NotSupportedException;
import it.angrybear.interfaces.IBearPlugin;
import it.angrybear.utils.ServerUtils;
import it.fulminazzo.reflectionutils.objects.ReflObject;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * A wrapper that represents a Task that should be
 * executed after the given duration. It supports
 * {@link TimerIntermediateAction} which allow
 * running actions at the given time frame.
 */
@Getter
@Setter
public class Task {
    protected final List<TimerIntermediateAction> intermediateActions;
    protected final Runnable action;
    protected Double duration;
    protected double counter;
    protected Consumer<Double> secondIntermediateAction;
    protected ReflObject<?> task;
    protected double interval;
    protected boolean paused;

    public Task(Runnable action, TimerIntermediateAction... intermediateActions) {
        this.action = action;
        this.duration = null;
        this.counter = 0;
        this.intermediateActions = new ArrayList<>();
        addIntermediateActions(intermediateActions);
        this.interval = 1;
    }

    public Task(IBearPlugin plugin, Runnable action, TimerIntermediateAction... intermediateActions) {
        this(plugin, 0, true, action, intermediateActions);
    }

    public Task(IBearPlugin plugin, double duration, Runnable action, TimerIntermediateAction... intermediateActions) {
        this(plugin, duration, true, action, intermediateActions);
    }

    public Task(IBearPlugin plugin, double duration, boolean async, Runnable action, TimerIntermediateAction... intermediateActions) {
        this(action, intermediateActions);
        start(plugin, duration, async);
    }

    /**
     * Start.
     *
     * @param plugin   the plugin
     * @param duration the duration
     */
    public void start(IBearPlugin plugin, double duration) {
        start(plugin, duration, true);
    }

    /**
     * Start.
     *
     * @param plugin   the plugin
     * @param duration the duration
     * @param async    the async
     */
    public void start(IBearPlugin plugin, double duration, boolean async) {
        if (task != null) return;
        this.duration = duration;
        Runnable counterRunnable = createCounterRunnable();
        String methodName;
        Object[] args = new Object[0];
        ReflObject<?> scheduler = ServerUtils.getScheduler();
        if (ServerUtils.isFolia())
            if (duration > 0) {
                methodName = "runAtFixedRate";
                args = new Object[]{plugin, (Consumer<?>) t -> counterRunnable.run(), 1L, (long) (interval * 20)};
            } else {
                methodName = "run";
                args = new Object[]{plugin, (Consumer<?>) t -> counterRunnable.run()};
            }
        else if (ServerUtils.isBukkit())
            if (duration > 0) {
                methodName = async ? "runTaskTimerAsynchronously" : "runTaskTimer";
                args = new Object[]{plugin, counterRunnable, 0L, (long) (interval * 20)};
            } else {
                methodName = async ? "runTaskAsynchronously" : "runTask";
                args = new Object[]{plugin, counterRunnable};
            }
        else if (ServerUtils.isVelocity()) {
            methodName = "schedule";
            scheduler = scheduler.callMethod("buildTask", plugin, counterRunnable);
            if (duration > 0)
                scheduler = scheduler.callMethod("delay", 0L, TimeUnit.SECONDS)
                        .callMethod("repeat", (long) (interval * 1000), TimeUnit.MILLISECONDS);
        } else if (ServerUtils.isBungeeCord())
            if (duration > 0) {
                methodName = "schedule";
                args = new Object[]{plugin, counterRunnable};
            } else {
                methodName = "runAsync";
                args = new Object[]{plugin, counterRunnable};
            }
        else throw new NotSupportedException();

        this.task = scheduler.callMethod(methodName, args);
        this.task.setShowErrors(false);
    }

    /**
     * Create the runnable responsible for the internal counter.
     *
     * @return the runnable
     */
    protected Runnable createCounterRunnable() {
        return () -> {
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
    }

    /**
     * Stop.
     */
    public void stop() {
        if (task != null) {
            task.callMethod("cancel");
            task = null;
        }
        counter = 0;
    }

    /**
     * Check if is stopped.
     *
     * @return true if stopped
     */
    public boolean isStopped() {
        if (task == null) return true;
        if (ServerUtils.isFolia()) {
            return task.getMethodObject("isCancelled");
        } else if (ServerUtils.isBukkit()) {
            ReflObject<?> scheduler = ServerUtils.getScheduler();
            boolean queued = scheduler.getMethodObject("isQueued", getId());
            boolean running = scheduler.getMethodObject("isCurrentlyRunning", getId());
            return !queued && !running;
        } else if (ServerUtils.isVelocity()) {
            return !task.callMethod("status").getMethodObject("name").equals("SCHEDULED");
        } else return false;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public int getId() {
        return task == null ? -1 : (ServerUtils.isVelocity() || ServerUtils.isFolia()) ? -1 :
                task.getMethodObject(ServerUtils.isBukkit() ? "getTaskId" : "getId");
    }

    /**
     * Add intermediate actions.
     *
     * @param intermediateActions the intermediate actions
     */
    public void addIntermediateActions(TimerIntermediateAction... intermediateActions) {
        Arrays.stream(intermediateActions).forEach(i -> {
            removeIntermediateActions(i);
            this.intermediateActions.add(i);
        });
    }

    /**
     * Remove intermediate actions.
     *
     * @param intermediateActions the intermediate actions
     */
    public void removeIntermediateActions(TimerIntermediateAction... intermediateActions) {
        removeIntermediateActions(Arrays.stream(intermediateActions)
                .map(TimerIntermediateAction::getTime)
                .toArray(Double[]::new));
    }

    /**
     * Remove intermediate actions.
     *
     * @param intermediateTimes the intermediate times
     */
    public void removeIntermediateActions(Double... intermediateTimes) {
        Arrays.stream(intermediateTimes).filter(Objects::nonNull).forEach(i ->
                this.intermediateActions.removeIf(a -> a.getTime() == i));
    }

    /**
     * Gets an intermediate action from the given time.
     *
     * @param intermediateTime the intermediate time
     * @return the intermediate action
     */
    public Optional<TimerIntermediateAction> getIntermediateAction(double intermediateTime) {
        return this.intermediateActions.stream().filter(a -> a.getTime() == intermediateTime).findAny();
    }

    /**
     * Sets the interval (because of Minecraft limitations,
     * the minimum is capped at 50s or one tick).
     *
     * @param interval the interval
     */
    public void setInterval(double interval) {
        if (interval < 0.05) interval = 0.05;
        this.interval = interval;
    }
}