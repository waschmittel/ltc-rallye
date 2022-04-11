package de.flubba.rallye.lapevent;

import de.flubba.rallye.entity.Runner;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class LapBroadcaster {
    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();

    private static final List<LapBroadcastListener> listeners = new CopyOnWriteArrayList<>();

    public interface LapBroadcastListener {
        void receiveBroadcast(Runner runner, long lapTime);
    }

    public static void broadcast(final Runner runner, final long lapTime) {
        for (final LapBroadcastListener listener : listeners) {
            EXECUTOR.execute(() -> listener.receiveBroadcast(runner, lapTime));
        }
    }

    public static void register(LapBroadcastListener listener) {
        listeners.add(listener);
    }

    public static void unregister(LapBroadcastListener listener) {
        listeners.remove(listener);
    }

    private LapBroadcaster() {
    }

}
