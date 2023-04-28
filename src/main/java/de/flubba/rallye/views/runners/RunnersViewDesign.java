package de.flubba.rallye.views.runners;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import de.flubba.generated.i18n.I18n;
import de.flubba.rallye.component.RunnersGrid;
import de.flubba.rallye.entity.Runner;
import de.flubba.rallye.entity.Sponsor;
import de.flubba.rallye.entity.Sponsor.Fields;

import java.util.List;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY;
import static com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.CENTER;

abstract class RunnersViewDesign extends SplitLayout {
    private final HorizontalLayout runnersToolbar = new HorizontalLayout();
    private final HorizontalLayout sponsorsToolbar = new HorizontalLayout();

    private final VerticalLayout runnersLayout = new VerticalLayout();
    private final VerticalLayout sponsorsLayout = new VerticalLayout();

    protected final Button addRunnerButton = new Button(I18n.RUNNER_BUTTON_ADD.get(), VaadinIcon.PLUS.create());
    protected final Button refreshButton = new Button(I18n.RUNNER_BUTTON_REFRESH.get(), VaadinIcon.REFRESH.create());
    protected final Button addSponsorButton = new Button(I18n.SPONSOR_BUTTON_ADD.get(), VaadinIcon.PLUS.create());

    protected final Grid<Sponsor> sponsorsGrid = new Grid<>(Sponsor.class);
    protected final RunnersGrid runnersGrid;

    protected RunnersViewDesign(RunnersGrid runnersGrid) {
        this.runnersGrid = runnersGrid;

        setOrientation(Orientation.VERTICAL);

        initRunnersLayout();
        initSponsorsLayout();

        addToPrimary(runnersLayout);
        addToSecondary(sponsorsLayout);

        setSizeFull();

        setPrimaryStyle("min-height", "25%");
        setSecondaryStyle("min-height", "25%");
    }

    private void initRunnersLayout() {
        initRunnersGrid();
        initRunnersToolbar();

        runnersLayout.add(runnersToolbar);
        runnersToolbar.setWidthFull();
        runnersLayout.addAndExpand(runnersGrid);
    }

    private void initRunnersToolbar() {
        runnersToolbar.setJustifyContentMode(CENTER);
        addRunnerButton.addThemeVariants(LUMO_PRIMARY);

        runnersToolbar.add(addRunnerButton, refreshButton);
    }

    private void initRunnersGrid() {
        runnersGrid.removeColumnByKey(Runner.Fields.bonusLaps);
        runnersGrid.removeColumnByKey(Runner.Fields.numberOfLapsRun);
        runnersGrid.removeColumnByKey(Runner.Fields.donations);
        runnersGrid.removeColumnByKey(Runner.Fields.numberOfSponsors);
        runnersGrid.removeColumnByKey(Runner.Fields.average);
    }

    private void initSponsorsLayout() {
        initSponsorsGrid();
        initSponsorsToolbar();

        sponsorsLayout.add(sponsorsToolbar);
        sponsorsToolbar.setWidthFull();
        sponsorsLayout.addAndExpand(sponsorsGrid);
    }

    private void initSponsorsToolbar() {
        sponsorsToolbar.setJustifyContentMode(CENTER);
        addSponsorButton.addThemeVariants(LUMO_PRIMARY);

        sponsorsToolbar.add(addSponsorButton);
        addSponsorButton.setEnabled(false);
    }

    private void initSponsorsGrid() {
        sponsorsGrid.setSizeFull();
        sponsorsGrid.removeColumnByKey(Fields.runnerId);
        sponsorsGrid.removeColumnByKey(Fields.id);
        sponsorsGrid.removeColumnByKey(Fields.totalDonation);
        sponsorsGrid.getColumnByKey(Fields.name).setResizable(false).setFlexGrow(3);
        sponsorsGrid.removeColumnByKey(Fields.street);
        sponsorsGrid.removeColumnByKey(Fields.city);
        sponsorsGrid.removeColumnByKey(Fields.country);
        sponsorsGrid.getColumnByKey(Fields.perLapDonation).setResizable(false).setFlexGrow(1).setWidth("130px");
        sponsorsGrid.getColumnByKey(Fields.oneTimeDonation).setResizable(false).setFlexGrow(1).setWidth("130px");

        sponsorsGrid.setColumnOrder(List.of(
                sponsorsGrid.getColumnByKey(Fields.name),
                sponsorsGrid.getColumnByKey(Fields.perLapDonation),
                sponsorsGrid.getColumnByKey(Fields.oneTimeDonation)
        ));

        sponsorsGrid.setSelectionMode(Grid.SelectionMode.NONE);
    }
}
