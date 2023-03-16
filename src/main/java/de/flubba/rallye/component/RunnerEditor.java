package de.flubba.rallye.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToLongConverter;
import de.flubba.generated.i18n.I18n;
import de.flubba.rallye.entity.Runner;
import de.flubba.rallye.entity.Runner.Fields;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public final class RunnerEditor {

    private RunnerEditor() {
    }

    public static void edit(Runner runner, Predicate<Runner> submitHandler) {
        new BeanEditDialog<>(runner, Runner.class, RunnerEditor::bindFields, submitHandler);
    }

    private static Optional<List<Component>> bindFields(Binder<Runner> binder, Runner runner) {
        var id = new TextField(I18n.RUNNER_ID.get());
        id.setEnabled(false);
        id.setVisible(runner.getId() != null);
        binder.forField(id)
                .withNullRepresentation("")
                .withConverter(new StringToLongConverter(""))
                .bind(Fields.id);

        var name = new TextField(I18n.RUNNER_NAME.get());
        binder.forField(name).bind(Fields.name);

        var roomNumber = new TextField(I18n.RUNNER_ROOM.get());
        binder.forField(roomNumber).bind(Fields.roomNumber);

        var bonusLaps = new NumberField(I18n.RESULTS_BONUS_LAPS.get());
        bonusLaps.setVisible(false);
        binder.forField(bonusLaps).bind(Fields.bonusLaps);

        var gender = new Select<Runner.Gender>();
        gender.setLabel(I18n.RUNNER_GENDER.get());
        gender.setItems(Runner.Gender.values());
        binder.forField(gender).bind(Fields.gender);

        var country = new Select<Runner.Country>();
        country.setLabel(I18n.RUNNER_COUNTRY.get());
        country.setItems(Runner.Country.values());
        binder.forField(country).bind(Fields.country);

        return Optional.empty();
    }

    /*TODO public void showResultFields() {
        name.setEnabled(false);
        roomNumber.setVisible(false);
        gender.setVisible(false);
        country.setVisible(false);
        bonusLaps.setVisible(true);
    }*/

    /*protected void bind() {
        getBinder().forField(bonusLaps)
                .withConverter(new StringToLongConverter(I18n.RESULTS_BONUS_LAPS_NON_INTEGER.get()))
                .withNullRepresentation(0L)
                .bind("bonusLaps");
        super.bind();
    }*/

}
