package de.flubba.rallye.component;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import de.flubba.generated.i18n.I18n;
import de.flubba.rallye.entity.Runner;
import de.flubba.rallye.entity.repository.RunnerRepository;

import java.util.LinkedList;
import java.util.List;

import static de.flubba.rallye.entity.Runner.Fields.average;
import static de.flubba.rallye.entity.Runner.Fields.bonusPoints;
import static de.flubba.rallye.entity.Runner.Fields.country;
import static de.flubba.rallye.entity.Runner.Fields.donations;
import static de.flubba.rallye.entity.Runner.Fields.gender;
import static de.flubba.rallye.entity.Runner.Fields.id;
import static de.flubba.rallye.entity.Runner.Fields.name;
import static de.flubba.rallye.entity.Runner.Fields.numberOfLapsRun;
import static de.flubba.rallye.entity.Runner.Fields.numberOfSponsors;
import static de.flubba.rallye.entity.Runner.Fields.roomNumber;

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
        addSelectionListener(event ->
                event.getFirstSelectedItem().ifPresentOrElse(runner -> {
                    selectedRunner = runner;
                    selectionListeners.forEach(listener -> listener.onSelect(runner));
                }, () -> {
                    selectedRunner = null;
                    selectionListeners.forEach(listener -> listener.onSelect(null));
                }));
    }

    private void initColumns() {
        setColumnOrder(List.of(
                getColumnByKey(id).setResizable(false).setFlexGrow(0).setWidth("130px"),
                getColumnByKey(name).setResizable(false).setFlexGrow(1),
                getColumnByKey(gender).setResizable(false).setFlexGrow(0).setWidth("130px"),
                getColumnByKey(country).setResizable(false).setFlexGrow(0).setWidth("130px"),
                getColumnByKey(numberOfSponsors).setHeader("Sponsors").setResizable(false).setFlexGrow(0).setWidth("130px"),
                getColumnByKey(numberOfLapsRun).setHeader("Laps").setResizable(false).setFlexGrow(0).setWidth("130px"),
                getColumnByKey(bonusPoints).setResizable(false).setFlexGrow(0).setWidth("130px"),
                getColumnByKey(average).setResizable(false).setFlexGrow(0).setWidth("130px"),
                getColumnByKey(donations).setResizable(false).setFlexGrow(0).setWidth("130px"),
                getColumnByKey(roomNumber).setResizable(false).setFlexGrow(0).setWidth("200px")
        ));
    }

    private void initHeaderRow() {
        HeaderRow runnersHeader = appendHeaderRow();

        runnersHeader.getCell(getColumnByKey(name)).setComponent(runnersFilter);
        runnersFilter.setWidthFull();
        runnersFilter.setClearButtonVisible(true);
        runnersFilter.setValueChangeMode(ValueChangeMode.TIMEOUT);
        runnersFilter.setValueChangeTimeout(1000);
        runnersFilter.addValueChangeListener(e -> refresh());
        runnersFilter.setPrefixComponent(VaadinIcon.SEARCH.create());
        runnersFilter.setPlaceholder(I18n.RUNNER_FILTER_PLACEHOLDER.get());
    }

    public void refresh() {
        setItems(new ListDataProvider<>(
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
        int i = 0;
        for (var runnerInRow : getGenericDataView().getItems().toList()) {
            if (runnerInRow.equals(runner)) {
                scrollToIndex(i);
                break;
            }
            i++;
        }
    }

    public void addRunnerSelectionListener(SelectionListener selectionListener) {
        selectionListeners.add(selectionListener);
    }
}
