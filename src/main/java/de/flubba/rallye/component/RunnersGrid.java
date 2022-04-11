package de.flubba.rallye.component;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import de.flubba.rallye.entity.Runner;
import de.flubba.rallye.entity.repository.RunnerRepository;

import java.util.LinkedList;
import java.util.List;

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
        removeColumnByKey(Runner.Fields.sponsors);
        removeColumnByKey(Runner.Fields.laps);
        setColumnOrder(
                getColumnByKey(Runner.Fields.id).setResizable(false).setFlexGrow(0).setWidth("130px"),
                getColumnByKey(Runner.Fields.name).setResizable(false).setFlexGrow(1),
                getColumnByKey(Runner.Fields.gender).setResizable(false).setFlexGrow(0).setWidth("130px"),
                getColumnByKey(Runner.Fields.numberOfSponsors).setResizable(false).setFlexGrow(0).setWidth("130px"),
                getColumnByKey(Runner.Fields.numberOfLapsRun).setResizable(false).setFlexGrow(0).setWidth("130px"),
                getColumnByKey(Runner.Fields.bonusLaps).setResizable(false).setFlexGrow(0).setWidth("130px"),
                getColumnByKey(Runner.Fields.average).setResizable(false).setFlexGrow(0).setWidth("130px"),
                getColumnByKey(Runner.Fields.donations).setResizable(false).setFlexGrow(0).setWidth("130px"),
                getColumnByKey(Runner.Fields.roomNumber).setResizable(false).setFlexGrow(0).setWidth("200px")
        );
    }

    private void initHeaderRow() {
        HeaderRow runnersHeader = appendHeaderRow();

        runnersHeader.getCell(getColumnByKey(Runner.Fields.name)).setComponent(runnersFilter);
        runnersFilter.setWidthFull();
        runnersFilter.setClearButtonVisible(true);
        runnersFilter.setValueChangeMode(ValueChangeMode.TIMEOUT);
        runnersFilter.setValueChangeTimeout(1000);
        runnersFilter.addValueChangeListener(e -> refresh());
        runnersFilter.setPrefixComponent(VaadinIcon.SEARCH.create());
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
