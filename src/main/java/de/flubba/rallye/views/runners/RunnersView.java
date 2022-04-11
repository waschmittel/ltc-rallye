package de.flubba.rallye.views.runners;

import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import de.flubba.generated.i18n.I18n;
import de.flubba.rallye.component.ConfirmDialog;
import de.flubba.rallye.component.EditDeleteButtonsProvider;
import de.flubba.rallye.component.RunnerEditForm;
import de.flubba.rallye.component.RunnersGrid;
import de.flubba.rallye.component.SponsorEditForm;
import de.flubba.rallye.configuration.RallyeProperties;
import de.flubba.rallye.entity.Runner;
import de.flubba.rallye.entity.Sponsor;
import de.flubba.rallye.entity.repository.RunnerRepository;
import de.flubba.rallye.entity.repository.SponsorRepository;
import de.flubba.rallye.views.MainLayout;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.WordUtils;

import javax.annotation.PostConstruct;
import java.util.LinkedList;

import static com.vaadin.flow.component.notification.Notification.Position.MIDDLE;

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
    }

    @SuppressWarnings("squid:S2177") //this is intentional
    @PostConstruct
    private void init() {
        addRunnerButton.addClickListener(event -> editRunner(new Runner()));

        addSponsorButton.addClickListener(event -> {
            Sponsor newSponsor = new Sponsor();
            log.info("runner for sponsor: {}", runnersGrid.getSelectedRunner().getId());
            newSponsor.setRunner(runnersGrid.getSelectedRunner());
            editSponsor(newSponsor);
        });

        EditDeleteButtonsProvider.addAsFirst(sponsorsGrid, new EditDeleteButtonsProvider<>(this::editSponsor, this::confirmDeleteSponsor));
        EditDeleteButtonsProvider.addAsFirst(runnersGrid, new EditDeleteButtonsProvider<>(this::editRunner));

        refreshButton.addClickListener(e -> runnersGrid.refresh());

        runnersGrid.addRunnerSelectionListener(this::showSponsorsFor);
    }

    private void showSponsorsFor(Runner runner) {
        if (runner == null) {
            addSponsorButton.setEnabled(false);
            addSponsorButton.setText(I18n.SPONSOR_BUTTON_ADD.get());
            sponsorsGrid.setDataProvider(new ListDataProvider<>(new LinkedList<>()));
        } else {
            addSponsorButton.setEnabled(true);
            addSponsorButton.setText(I18n.SPONSOR_BUTTON_NAMED_ADD.get(runner.getName()));
            sponsorsGrid.setDataProvider(new ListDataProvider<>(sponsorRepository.findByRunner(runner)));
        }
    }

    private void editRunner(Runner runner) {
        RunnerEditForm runnerEditForm = new RunnerEditForm(runner);
        runnerEditForm.openInModalPopup();
        runnerEditForm.setSavedHandler(entity -> {
            if (saveRunner(runner)) {
                runnerEditForm.closePopup();
                addSponsorButton.focus();
            }
        });
        runnerEditForm.setResetHandler(editedServer -> {
            runnersGrid.refresh();
            runnerEditForm.closePopup();
        });
    }

    private boolean saveRunner(Runner runner) {
        sanitizeRunner(runner);
        if (runner.getId() == null && runnerRepository.existsByName(runner.getName())) {
            var notification = new Notification(I18n.RUNNER_DUPLICATE_ERROR.get(runner.getName()), 5000);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setPosition(MIDDLE);
            notification.open();
            return false;
        } else {
            var savedRunner = runnerRepository.saveAndFlush(runner);
            runnersGrid.selectRunner(savedRunner);
            return true;
        }
    }

    private void sanitizeRunner(Runner runner) {
        runner.setName(capitalize(runner.getName().trim()));
        runner.setRoomNumber(runner.getRoomNumber().trim());
    }

    private static String capitalize(String string) {
        return WordUtils.capitalizeFully(string, ' ', '-');
    }


    private void confirmDeleteSponsor(Sponsor sponsor) {
        new ConfirmDialog(I18n.SPONSOR_DELETE_QUESTION.get(sponsor.getName()),
                I18n.SPONSOR_DELETE_CONFIRM.get(),
                I18n.SPONSOR_DELETE_CANCEL.get(),
                () -> deleteSponsor(sponsor));
    }

    private void deleteSponsor(Sponsor sponsor) {
        sponsorRepository.delete(sponsor);
        showSponsorsFor(sponsor.getRunner());
    }

    private void editSponsor(Sponsor sponsor) {
        SponsorEditForm sponsorEditForm = new SponsorEditForm(sponsor, rallyeProperties.getShekelToEuroRate());
        sponsorEditForm.openInModalPopup();
        sponsorEditForm.getPopup().setWidth("400px");
        sponsorEditForm.setSavedHandler(entity -> {
            saveSponsor(sponsor);
            sponsorEditForm.closePopup();
            addSponsorButton.focus();
        });
        sponsorEditForm.setResetHandler(editedServer -> {
            showSponsorsFor(sponsor.getRunner());
            sponsorEditForm.closePopup();
        });
    }

    private void saveSponsor(Sponsor sponsor) {
        sanitizeSponsor(sponsor);
        sponsorRepository.saveAndFlush(sponsor);
        showSponsorsFor(sponsor.getRunner());
    }

    private void sanitizeSponsor(Sponsor sponsor) {
        sponsor.setName(capitalize(sponsor.getName().trim()));
        sponsor.setStreet(capitalize(sponsor.getStreet().trim()));
        sponsor.setCity(capitalize(sponsor.getCity().trim()));
        sponsor.setCountry(capitalize(sponsor.getCountry().trim()));
    }
}
