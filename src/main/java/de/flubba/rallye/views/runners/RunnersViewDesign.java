package de.flubba.rallye.views.runners;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import de.flubba.generated.i18n.I18n;
import de.flubba.rallye.component.RunnersGrid;
import de.flubba.rallye.entity.Sponsor;

import javax.annotation.PostConstruct;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY;
import static com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.CENTER;

public class RunnersViewDesign extends SplitLayout {
    private final HorizontalLayout runnersToolbar = new HorizontalLayout();
    private final HorizontalLayout sponsorsToolbar = new HorizontalLayout();

    private final VerticalLayout runnersLayout = new VerticalLayout();
    private final VerticalLayout sponsorsLayout = new VerticalLayout();

    protected final Button addRunnerButton = new Button(I18n.RUNNER_BUTTON_ADD.get(), VaadinIcon.PLUS.create());
    protected final Button refreshButton = new Button(I18n.RUNNER_BUTTON_REFRESH.get(), VaadinIcon.REFRESH.create());
    protected final Button addSponsorButton = new Button(I18n.SPONSOR_BUTTON_ADD.get(), VaadinIcon.PLUS.create());

    protected final Grid<Sponsor> sponsorsGrid = new Grid<>(Sponsor.class);
    protected final RunnersGrid runnersGrid;

    public RunnersViewDesign(RunnersGrid runnersGrid) {
        this.runnersGrid = runnersGrid;
    }

    @PostConstruct //TODO: is this still the right way to do lazy initialization?
    private void init() {
        setOrientation(Orientation.VERTICAL);

        initRunnersLayout();
        initSponsorsLayout();

        addToPrimary(runnersLayout);
        addToSecondary(sponsorsLayout);

        setSizeFull();
        //TODO: min/max split position
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
        runnersGrid.removeColumnByKey("bonusLaps");
        runnersGrid.removeColumnByKey("donations");
        runnersGrid.removeColumnByKey("numberOfSponsors");
        runnersGrid.removeColumnByKey("average");
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
        sponsorsGrid.removeColumnByKey("runner");
        sponsorsGrid.removeColumnByKey("id");
        sponsorsGrid.removeColumnByKey("totalDonation");
        sponsorsGrid.getColumnByKey("name").setResizable(false).setFlexGrow(1);
        sponsorsGrid.getColumnByKey("street").setResizable(false).setFlexGrow(1);
        sponsorsGrid.getColumnByKey("city").setResizable(false).setFlexGrow(1);
        sponsorsGrid.getColumnByKey("country").setResizable(false).setFlexGrow(1);
        sponsorsGrid.getColumnByKey("perLapDonation").setResizable(false).setWidth("100px");
        sponsorsGrid.getColumnByKey("oneTimeDonation").setResizable(false).setWidth("100px");
        sponsorsGrid.setColumnOrder(
                sponsorsGrid.getColumnByKey("name"), //TODO: do this better
                sponsorsGrid.getColumnByKey("street"),
                sponsorsGrid.getColumnByKey("city"),
                sponsorsGrid.getColumnByKey("country"),
                sponsorsGrid.getColumnByKey("perLapDonation"),
                sponsorsGrid.getColumnByKey("oneTimeDonation")
        );
        sponsorsGrid.setSelectionMode(Grid.SelectionMode.NONE);
    }
}
