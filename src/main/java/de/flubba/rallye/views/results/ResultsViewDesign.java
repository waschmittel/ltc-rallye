package de.flubba.rallye.views.results;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.flubba.generated.i18n.I18n;
import de.flubba.rallye.component.RunnersGrid;
import de.flubba.rallye.entity.Runner;
import de.flubba.rallye.views.ViewToolbar;

abstract class ResultsViewDesign extends VerticalLayout {
    private final HorizontalLayout toolbarLayout = new ViewToolbar("Results");

    protected final Button refreshButton = new Button(I18n.RESULTS_REFRESH.get(), VaadinIcon.REFRESH.create());
    protected final Button calculateButton = new Button(I18n.RESULTS_CALCULATE.get(), VaadinIcon.TROPHY.create());
    protected final Anchor exportRunnersAnchor;
    protected final Anchor exportSponsorsAnchor;

    protected final RunnersGrid runnersGrid;

    protected ResultsViewDesign(RunnersGrid runnersGrid) {
        this.runnersGrid = runnersGrid;

        // Create export buttons inside anchors
        Button exportRunnersButton = new Button("Export Runners's Results", VaadinIcon.TABLE.create());
        exportRunnersButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        exportRunnersAnchor = new Anchor();
        exportRunnersAnchor.add(exportRunnersButton);

        Button exportSponsorsButton = new Button("Export Sponsor Letter Data", VaadinIcon.INVOICE.create());
        exportSponsorsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        exportSponsorsAnchor = new Anchor();
        exportSponsorsAnchor.add(exportSponsorsButton);

        toolbarLayout.add(refreshButton, calculateButton, exportRunnersAnchor, exportSponsorsAnchor);

        runnersGrid.removeColumn(runnersGrid.getColumnByKey(Runner.Fields.roomNumber));
        runnersGrid.setSelectionMode(Grid.SelectionMode.NONE);

        add(toolbarLayout);
        addAndExpand(runnersGrid);
    }
}
