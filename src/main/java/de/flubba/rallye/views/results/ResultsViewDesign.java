package de.flubba.rallye.views.results;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.flubba.generated.i18n.I18n;
import de.flubba.rallye.component.RunnersGrid;
import de.flubba.rallye.entity.Runner;

abstract class ResultsViewDesign extends VerticalLayout {
    private final HorizontalLayout toolbarLayout = new HorizontalLayout();

    protected final Button refreshButton = new Button(I18n.RESULTS_REFRESH.get(), VaadinIcon.REFRESH.create());
    protected final Button calculateButton = new Button(I18n.RESULTS_CALCULATE.get(), VaadinIcon.TROPHY.create());

    protected final RunnersGrid runnersGrid;

    protected ResultsViewDesign(RunnersGrid runnersGrid) {
        this.runnersGrid = runnersGrid;
        
        toolbarLayout.add(refreshButton, calculateButton);

        runnersGrid.removeColumn(runnersGrid.getColumnByKey(Runner.Fields.roomNumber));
        runnersGrid.setSelectionMode(Grid.SelectionMode.NONE);

        add(toolbarLayout);
        addAndExpand(runnersGrid);
    }
}
