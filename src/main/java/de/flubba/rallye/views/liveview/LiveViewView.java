package de.flubba.rallye.views.liveview;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.flubba.rallye.entity.Runner;
import de.flubba.rallye.service.LapBroadcaster;
import de.flubba.rallye.views.MainLayout;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;

@PageTitle("Live View")
@Route(value = "Live-View", layout = MainLayout.class)
@RequiredArgsConstructor
public class LiveViewView extends LiveViewDesign {
    public static final int MAX_DISPLAYED_LAPS = 10;

    private final LinkedList<Text> recent = new LinkedList<>();
    private LapBroadcaster.LapBroadcastListener lapBroadcastListener;
    private final LapBroadcaster lapBroadcaster;

    private void addRunner(String name) {
        Text newText = new Text(name);
        //TODO: newText.addStyleName("liveLap");
        if (recent.size() >= MAX_DISPLAYED_LAPS) {
            remove(recent.pop());
        }
        addComponentAsFirst(newText);
        recent.add(newText);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        var ui = attachEvent.getUI();
        lapBroadcastListener = lapBroadcaster.register((runner, lapTime) -> ui.access(() -> addLap(runner, lapTime)));
    }

    private void addLap(Runner runner, long lapTime) {
        BigDecimal seconds = new BigDecimal(lapTime)
                .divide(new BigDecimal(1000), RoundingMode.HALF_UP)
                .setScale(1, RoundingMode.HALF_UP);
        addRunner(String.format("%ss - %s - %s", seconds.toString(), runner.getId(), runner.getName()));
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (lapBroadcastListener != null) {
            lapBroadcaster.unregister(lapBroadcastListener);
        }
    }
}
