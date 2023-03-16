package de.flubba.rallye.views.runners;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.GridSortOrderBuilder;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import de.flubba.generated.i18n.I18n;
import de.flubba.rallye.component.DeleteConfirmDialog;
import de.flubba.rallye.component.EditDeleteButtonsProvider;
import de.flubba.rallye.component.RunnerEditor;
import de.flubba.rallye.component.RunnersGrid;
import de.flubba.rallye.component.SponsorEditor;
import de.flubba.rallye.configuration.RallyeProperties;
import de.flubba.rallye.entity.Runner;
import de.flubba.rallye.entity.Sponsor;
import de.flubba.rallye.entity.repository.RunnerRepository;
import de.flubba.rallye.entity.repository.SponsorRepository;
import de.flubba.rallye.views.MainLayout;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.WordUtils;

import java.util.LinkedList;
import java.util.Optional;

import static com.vaadin.flow.component.notification.Notification.Position.TOP_CENTER;
import static com.vaadin.flow.component.notification.NotificationVariant.LUMO_ERROR;

@PageTitle("Runners")
@Route(value = "runners", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@Uses(Icon.class)
@Slf4j
public class RunnersView extends RunnersViewDesign {
    private final RunnerRepository runnerRepository;
    private final SponsorRepository sponsorRepository;
    private final RallyeProperties rallyeProperties;

    public RunnersView(RallyeProperties rallyeProperties, RunnersGrid runnersGrid, RunnerRepository runnerRepository, SponsorRepository sponsorRepository) {
        super(runnersGrid);
        this.rallyeProperties = rallyeProperties;
        this.runnerRepository = runnerRepository;
        this.sponsorRepository = sponsorRepository;

        buildLayout();
    }

    private void buildLayout() {
        addRunnerButton.addClickListener(event -> editRunner(new Runner()));

        addSponsorButton.addClickListener(event -> {
            Sponsor newSponsor = new Sponsor();
            log.info("runner for sponsor: {}", runnersGrid.getSelectedRunner().getId());
            newSponsor.setRunnerId(runnersGrid.getSelectedRunner().getId());
            editSponsor(newSponsor);
        });

        EditDeleteButtonsProvider.addAsFirst(sponsorsGrid, new EditDeleteButtonsProvider<>(this::editSponsor, this::confirmDeleteSponsor));
        EditDeleteButtonsProvider.addAsFirst(runnersGrid, new EditDeleteButtonsProvider<>(this::editRunner));

        refreshButton.addClickListener(e -> runnersGrid.refresh());

        runnersGrid.addRunnerSelectionListener(runner -> showSponsorsForSelectedRunner());
        runnersGrid.sort(new GridSortOrderBuilder<Runner>().thenAsc(runnersGrid.getColumnByKey(Runner.Fields.id)).build());
        runnersGrid.refresh();

        sponsorsGrid.sort(new GridSortOrderBuilder<Sponsor>().thenAsc(sponsorsGrid.getColumnByKey(Sponsor.Fields.name)).build());
    }

    private void showSponsorsForSelectedRunner() {
        var runner = runnersGrid.asSingleSelect().getValue();
        if (runner == null) {
            addSponsorButton.setEnabled(false);
            addSponsorButton.setText(I18n.SPONSOR_BUTTON_ADD.get());
            sponsorsGrid.setItems(new ListDataProvider<>(new LinkedList<>()));
        } else {
            addSponsorButton.setEnabled(true);
            addSponsorButton.setText(I18n.SPONSOR_BUTTON_NAMED_ADD.get(runner.getName()));
            sponsorsGrid.setItems(new ListDataProvider<>(sponsorRepository.findByRunnerId(runner.getId())));
        }
    }

    private void editRunner(Runner runner) {
        RunnerEditor.edit(runner, summittedRunner -> {
            var optionalSavedRunner = saveRunner(summittedRunner);
            optionalSavedRunner.ifPresent(savedRunner -> {
                addSponsorButton.focus();
                runnersGrid.selectRunner(savedRunner);
            });
            return optionalSavedRunner.isPresent();
        });
    }

    private Optional<Runner> saveRunner(Runner runner) {
        sanitizeRunner(runner);
        if (runner.getId() == null && runnerRepository.existsByName(runner.getName())) {
            var notification = new Notification(I18n.RUNNER_DUPLICATE_ERROR.get(runner.getName()));
            UI.getCurrent().addToModalComponent(notification);
            notification.setPosition(TOP_CENTER);
            notification.setDuration(5000);
            notification.addThemeVariants(LUMO_ERROR);
            notification.open();
            return Optional.empty();
        }
        return Optional.of(runnerRepository.saveAndFlush(runner));
    }

    private void sanitizeRunner(Runner runner) {
        runner.setName(capitalize(runner.getName().trim()));
        runner.setRoomNumber(runner.getRoomNumber().trim());
    }

    private static String capitalize(String string) {
        return WordUtils.capitalizeFully(string, ' ', '-', '/');
    }


    private void confirmDeleteSponsor(Sponsor sponsor) {
        new DeleteConfirmDialog(I18n.SPONSOR_DELETE_QUESTION.get(sponsor.getName()),
                I18n.SPONSOR_DELETE_CONFIRM.get(),
                I18n.SPONSOR_DELETE_CANCEL.get(),
                () -> deleteSponsor(sponsor));
    }

    private void deleteSponsor(Sponsor sponsor) {
        sponsorRepository.delete(sponsor);
        showSponsorsForSelectedRunner();
    }

    private void editSponsor(Sponsor sponsor) {
        new SponsorEditor(sponsor, rallyeProperties.getShekelToEuroRate(), submittedSponsor -> {
            saveSponsor(sponsor);
            addSponsorButton.focus();
            return true;
        });
    }

    private void saveSponsor(Sponsor sponsor) {
        sanitizeSponsor(sponsor);
        sponsorRepository.saveAndFlush(sponsor);
        showSponsorsForSelectedRunner();
    }

    private void sanitizeSponsor(Sponsor sponsor) {
        sponsor.setName(capitalize(sponsor.getName().trim()));
        sponsor.setStreet(capitalize(sponsor.getStreet().trim()));
        sponsor.setCity(capitalize(sponsor.getCity().trim()));
        sponsor.setCountry(capitalize(sponsor.getCountry().trim()));
    }
}
