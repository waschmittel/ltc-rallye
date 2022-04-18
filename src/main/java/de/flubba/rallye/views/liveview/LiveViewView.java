package de.flubba.rallye.views.liveview;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.flubba.rallye.entity.Runner;
import de.flubba.rallye.service.LapBroadcaster;
import de.flubba.rallye.views.MainLayout;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.LinkedList;

@PageTitle("Live View")
@Route(value = "Live-View", layout = MainLayout.class)
@RequiredArgsConstructor
public class LiveViewView extends LiveViewDesign {
    public static final int MAX_DISPLAYED_LAPS = 100;

    private final LinkedList<MessageListItem> laps = new LinkedList<>();

    private LapBroadcaster.LapBroadcastListener lapBroadcastListener;
    private final LapBroadcaster lapBroadcaster;

    private void addRunner(Runner runner, BigDecimal seconds) {
        laps.push(new MessageListItem(
                "Lap time: %ss".formatted(seconds),
                Instant.now(),
                runner.getName()
        ));
        if (laps.size() > MAX_DISPLAYED_LAPS) {
            laps.removeLast();
        }
        setItems(laps);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        var ui = attachEvent.getUI();
        lapBroadcastListener = lapBroadcaster.register(
                (runner, lapTime)
                        -> ui.access(() -> addLap(runner, lapTime)));
    }

    private void addLap(Runner runner, long lapTime) {
        BigDecimal seconds = new BigDecimal(lapTime)
                .setScale(1, RoundingMode.HALF_UP)
                .divide(new BigDecimal(1000), RoundingMode.HALF_UP);
        addRunner(runner, seconds);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (lapBroadcastListener != null) {
            lapBroadcaster.unregister(lapBroadcastListener);
        }
    }
}
