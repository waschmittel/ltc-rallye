package de.flubba.rallye.service;

import de.flubba.rallye.entity.Runner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class LapBroadcaster {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final List<LapBroadcastListener> listeners = new CopyOnWriteArrayList<>();

    public void broadcast(final Runner runner, final long lapTime) {
        for (final LapBroadcastListener listener : listeners) {
            executorService.execute(() -> listener.receiveBroadcast(runner, lapTime));
        }
    }

    public LapBroadcastListener register(LapBroadcastListener listener) {
        listeners.add(listener);
        return listener;
    }

    public void unregister(LapBroadcastListener listener) {
        listeners.remove(listener);
    }

    public interface LapBroadcastListener {
        void receiveBroadcast(Runner runner, long lapTime);
    }
}
