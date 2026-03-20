package de.flubba.rallye.views.results;

import com.vaadin.flow.component.grid.GridSortOrderBuilder;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.DownloadHandler;
import de.flubba.rallye.component.EditDeleteButtonsProvider;
import de.flubba.rallye.component.RunnerResultEditor;
import de.flubba.rallye.component.RunnersGrid;
import de.flubba.rallye.entity.Runner;
import de.flubba.rallye.entity.repository.RunnerRepository;
import de.flubba.rallye.service.ExcelExportService;
import de.flubba.rallye.service.RaceResultsService;
import de.flubba.rallye.util.ThrowingSupplier;
import de.flubba.rallye.views.MainLayout;

import java.io.IOException;
import java.io.InputStream;

import static de.flubba.rallye.Application.TITLE_SUFFIX;

@PageTitle("Results" + TITLE_SUFFIX)
@Route(value = "results", layout = MainLayout.class)
public class ResultsView extends ResultsViewDesign {
    private final RunnerRepository runnerRepository;
    private final RaceResultsService raceResultsService;
    private final ExcelExportService excelExportService;

    public ResultsView(RunnersGrid runnersGrid,
                       RunnerRepository runnerRepository,
                       RaceResultsService raceResultsService,
                       ExcelExportService excelExportService) {
        super(runnersGrid);
        this.runnerRepository = runnerRepository;
        this.raceResultsService = raceResultsService;
        this.excelExportService = excelExportService;

        buildLayout();
    }

    private void buildLayout() {
        EditDeleteButtonsProvider.addAsFirst(runnersGrid, new EditDeleteButtonsProvider<>(this::editRunner));
        calculateButton.addClickListener(e -> {
            raceResultsService.generateResults();
            runnersGrid.refresh();
        });
        refreshButton.addClickListener(e -> runnersGrid.refresh());

        exportRunnersAnchor.setHref(excelDownloadHandler("runners", excelExportService::exportRunnersToExcel));
        exportSponsorsAnchor.setHref(excelDownloadHandler("sponsors", excelExportService::exportSponsorsToExcel));

        runnersGrid.sort(new GridSortOrderBuilder<Runner>().thenAsc(runnersGrid.getColumnByKey(Runner.Fields.id)).build());
        runnersGrid.refresh();
    }

    private DownloadHandler excelDownloadHandler(String fileName, ThrowingSupplier<InputStream, IOException> excelAsStreamSupplier) {
        return event -> {
            try {
                event.setFileName(fileName + ".xlsx");
                event.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                InputStream inputStream = excelAsStreamSupplier.get();
                inputStream.transferTo(event.getOutputStream());
            } catch (IOException ex) {
                Notification.show("Error generating Excel file: " + ex.getMessage());
            }
        };
    }

    private void editRunner(Runner runner) {
        RunnerResultEditor.edit(runner, editedRunner -> {
            var savedRunner = runnerRepository.saveAndFlush(editedRunner);
            runnersGrid.selectRunner(savedRunner);
            return true;
        });
    }

}
