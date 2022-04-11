package de.flubba.rallye.component;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import de.flubba.rallye.entity.Runner;
import de.flubba.rallye.entity.repository.RunnerRepository;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

import static com.vaadin.flow.data.provider.SortDirection.ASCENDING;

@SpringComponent
@RouteScope
public class RunnersGrid extends Grid<Runner> {
    public interface SelectionListener {
        void onSelect(Runner runner);
    }

    private final TextField runnersFilter = new TextField();

    private final RunnerRepository repository;

    private final List<SelectionListener> selectionListeners = new LinkedList<>();
    private Runner selectedRunner = null;

    public RunnersGrid(RunnerRepository repository) {
        super(Runner.class);
        setSizeFull();
        initColumns();
        initHeaderRow();
        initSelection();
        this.repository = repository;
    }

    @PostConstruct
    private void init() {
        sort(List.of(new GridSortOrder<>(getColumnByKey("id"), ASCENDING)));
        refresh();
    }

    private void initSelection() {
        setSelectionMode(SelectionMode.SINGLE);
        addSelectionListener(event -> {
            event.getFirstSelectedItem().ifPresentOrElse(runner -> {
                selectedRunner = runner;
                selectionListeners.forEach(listener -> listener.onSelect(runner));
            }, () -> {
                selectedRunner = null;
                selectionListeners.forEach(listener -> listener.onSelect(null));
            });
        });
    }

    private void initColumns() {
        removeColumnByKey("sponsors");
        removeColumnByKey("laps");
        setColumnOrder(
                getColumnByKey("id").setResizable(false).setWidth("100px"),
                getColumnByKey("name").setResizable(false).setFlexGrow(1),
                getColumnByKey("gender").setResizable(false).setWidth("100px"),
                getColumnByKey("numberOfSponsors").setResizable(false).setWidth("100px"),
                getColumnByKey("numberOfLapsRun").setResizable(false).setWidth("100px"),
                getColumnByKey("bonusLaps").setResizable(false).setWidth("100px"),
                getColumnByKey("average").setResizable(false).setWidth("100px"),
                getColumnByKey("donations").setResizable(false).setWidth("100px"),
                getColumnByKey("roomNumber").setResizable(false).setWidth("100px")
        );
    }

    private void initHeaderRow() {
        HeaderRow runnersHeader = appendHeaderRow();

        runnersHeader.getCell(getColumnByKey("name")).setComponent(runnersFilter);
        runnersFilter.setWidthFull();
        runnersFilter.setClearButtonVisible(true);
        runnersFilter.setValueChangeMode(ValueChangeMode.TIMEOUT);
        runnersFilter.setValueChangeTimeout(1000);
        runnersFilter.addValueChangeListener(e -> refresh());
    }

    public void refresh() {
        setDataProvider(new ListDataProvider<>(
                repository.findByNameIgnoreCaseContaining(runnersFilter.getValue())));
        if (selectedRunner != null) {
            select(selectedRunner);
        }

    }

    public Runner getSelectedRunner() {
        return selectedRunner;
    }

    public void selectRunner(Runner runner) {
        selectedRunner = runner;
        refresh();
        runnersFilter.setValue("");
        select(runner);
    }

    public void addRunnerSelectionListener(SelectionListener selectionListener) {
        selectionListeners.add(selectionListener);
    }
}
