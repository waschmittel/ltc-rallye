package de.flubba.rallye.views.runners;

import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import de.flubba.generated.i18n.I18n;
import de.flubba.rallye.component.ConfirmDialog;
import de.flubba.rallye.component.EditDeleteButtonsProvider;
import de.flubba.rallye.component.ErrorDialog;
import de.flubba.rallye.component.RunnerEditForm;
import de.flubba.rallye.component.RunnersGrid;
import de.flubba.rallye.component.SponsorEditForm;
import de.flubba.rallye.entity.Runner;
import de.flubba.rallye.entity.Sponsor;
import de.flubba.rallye.entity.repository.RunnerRepository;
import de.flubba.rallye.entity.repository.SponsorRepository;
import de.flubba.rallye.views.MainLayout;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.LinkedList;

@PageTitle("Runners")
@Route(value = "runners", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class) //TODO: this is the default route - maybe change this
@Uses(Icon.class)
@Slf4j
public class RunnersView extends RunnersViewDesign {
    private final RunnerRepository runnerRepository;
    private final SponsorRepository sponsorRepository;

    //@Value("${de.flubba.rally.shekel-euro-rate}")
    private final BigDecimal shekelToEuro = new BigDecimal("0.22"); //TODO: get this from a config class

    public RunnersView(RunnersGrid runnersGrid, RunnerRepository runnerRepository, SponsorRepository sponsorRepository) {
        super(runnersGrid); //TODO: see if this can be constructed with lombok
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

        sponsorsGrid.addComponentColumn(new EditDeleteButtonsProvider<>(this::editSponsor, this::confirmDeleteSponsor)).setResizable(false) //TODO: dont add at end
                .setWidth("120px"); //TODO: maybe use a renderer
        runnersGrid.addComponentColumn(new EditDeleteButtonsProvider<>(this::editRunner)).setResizable(false).setWidth("100px"); //TODO: dont add at end

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
            saveRunner(runner);
            runnerEditForm.closePopup();
            addSponsorButton.focus();
        });
        runnerEditForm.setResetHandler(editedServer -> {
            runnersGrid.refresh();
            runnerEditForm.closePopup();
        });
    }

    private void saveRunner(Runner runner) {
        sanitizeRunner(runner);
        if (runner.getId() == null && runnerRepository.countByName(runner.getName()) > 0) {
            new ErrorDialog(String.format("Cannot create \"%s\". There is already a runner with this name.", runner.getName()));
        } else {
            var savedRunner = runnerRepository.saveAndFlush(runner);
            runnersGrid.selectRunner(savedRunner);
        }
    }

    private void sanitizeRunner(Runner runner) {
        runner.setName(capitalize(runner.getName().trim()));
        runner.setRoomNumber(runner.getRoomNumber().trim());
    }

    private static String capitalize(String string) {
        return WordUtils.capitalizeFully(string, ' ', '-'); //TODO: replace
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
        SponsorEditForm sponsorEditForm = new SponsorEditForm(sponsor, shekelToEuro);
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
