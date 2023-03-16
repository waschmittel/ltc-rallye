package de.flubba.rallye.views.results;

import com.vaadin.flow.component.grid.GridSortOrderBuilder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.flubba.rallye.component.EditDeleteButtonsProvider;
import de.flubba.rallye.component.RunnersGrid;
import de.flubba.rallye.entity.Runner;
import de.flubba.rallye.entity.repository.RunnerRepository;
import de.flubba.rallye.service.RaceResultsService;
import de.flubba.rallye.views.MainLayout;

@PageTitle("Results")
@Route(value = "results", layout = MainLayout.class)
public class ResultsView extends ResultsViewDesign {
    private final RunnerRepository runnerRepository;

    private final RaceResultsService raceResultsService;

    public ResultsView(RunnersGrid runnersGrid, RunnerRepository runnerRepository, RaceResultsService raceResultsService) {
        super(runnersGrid);
        this.runnerRepository = runnerRepository;
        this.raceResultsService = raceResultsService;

        buildLayout();
    }

    private void buildLayout() {
        EditDeleteButtonsProvider.addAsFirst(runnersGrid, new EditDeleteButtonsProvider<>(this::editRunner));
        calculateButton.addClickListener(e -> {
            raceResultsService.generateResults();
            runnersGrid.refresh();
        });
        refreshButton.addClickListener(e -> runnersGrid.refresh());
        runnersGrid.sort(new GridSortOrderBuilder<Runner>().thenAsc(runnersGrid.getColumnByKey(Runner.Fields.id)).build());
        runnersGrid.refresh();
    }

    private void editRunner(Runner runner) {
        //TODO new RunnerEditDialog(runner); //TODO
        //TODO runnerEditDialog.showResultFields();
        /* TODO runnerEditForm.openInModalPopup();
        runnerEditForm.setSavedHandler(entity -> {
            saveRunner(runner);
            runnerEditForm.closePopup();
        });
        runnerEditForm.setResetHandler(editedServer -> {
            runnersGrid.refresh();
            runnerEditForm.closePopup();
        });*/
    }

    private void saveRunner(Runner runner) {
        runnersGrid.selectRunner(runnerRepository.saveAndFlush(runner));
    }

}
