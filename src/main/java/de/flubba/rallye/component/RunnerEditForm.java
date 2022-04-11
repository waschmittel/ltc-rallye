package de.flubba.rallye.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.converter.StringToLongConverter;
import com.vaadin.flow.data.value.ValueChangeMode;
import de.flubba.generated.i18n.I18n;
import de.flubba.rallye.entity.Runner;
import org.vaadin.firitin.form.AbstractForm;

import java.util.EnumSet;

public class RunnerEditForm extends AbstractForm<Runner> {
    private final TextField id = new TextField(I18n.RUNNER_ID.get());
    private final TextField name = new TextField(I18n.RUNNER_NAME.get());
    private final TextField roomNumber = new TextField(I18n.RUNNER_ROOM.get());
    private final TextField bonusLaps = new TextField(I18n.RESULTS_BONUS_LAPS.get());
    private final ComboBox<Runner.Gender> gender = new ComboBox<>(I18n.RUNNER_GENDER.get(), EnumSet.allOf(Runner.Gender.class));

    public RunnerEditForm(Runner runner) {
        super(Runner.class);

        id.setEnabled(false);
        if (runner.getId() == null) {
            id.setVisible(false);
        }

        bonusLaps.setVisible(false);

        roomNumber.setValueChangeMode(ValueChangeMode.EAGER); //so that the "Save" button becomes active early enough

        setSaveCaption(I18n.RUNNER_FORM_BUTTON_SAVE.get());
        setCancelCaption(I18n.RUNNER_FORM_BUTTON_CANCEL.get());
        getDeleteButton().setVisible(false);

        getContent().setWidth("400px");

        setEntity(runner);
    }

    public void showResultFields() {
        name.setEnabled(false);
        roomNumber.setVisible(false);
        gender.setVisible(false);
        bonusLaps.setVisible(true);
    }

    @Override
    protected void bind() {
        getBinder().forField(id)
                .withConverter(new StringToLongConverter("auto-generated"))
                .withNullRepresentation(0L)
                .bind("id");
        getBinder().forField(bonusLaps)
                .withConverter(new StringToLongConverter(I18n.RESULTS_BONUS_LAPS_NON_INTEGER.get()))
                .withNullRepresentation(0L)
                .bind("bonusLaps");
        super.bind();
    }


    @Override
    protected Component createContent() {
        return new VerticalLayout(new FormLayout(name, gender, roomNumber, bonusLaps, id), getToolbar());
    }
}
