package de.flubba.rallye.service;

import de.flubba.rallye.entity.Runner;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class LapBroadcaster { //TODO: make this a service
    public interface LapBroadcastListener {
        void receiveBroadcast(Runner runner, long lapTime);
    }

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static final List<LapBroadcastListener> listeners = new CopyOnWriteArrayList<>();

    public static void broadcast(final Runner runner, final long lapTime) {
        for (final LapBroadcastListener listener : listeners) {
            executorService.execute(() -> listener.receiveBroadcast(runner, lapTime));
        }
    }

    public static LapBroadcastListener register(LapBroadcastListener listener) {
        listeners.add(listener);
        return listener;
    }

    public static void unregister(LapBroadcastListener listener) {
        listeners.remove(listener);
    }

    private LapBroadcaster() {
    }

}
