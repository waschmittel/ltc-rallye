package de.flubba.rallye.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.HasValueChangeMode;
import de.flubba.generated.i18n.I18n;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.vaadin.flow.component.Key.ENTER;
import static com.vaadin.flow.component.Unit.PIXELS;
import static com.vaadin.flow.data.value.ValueChangeMode.EAGER;

public class BeanEditDialog<T> extends Dialog {
    private final T bean;
    private final Binder<T> binder;
    private final Predicate<T> submitHandler;

    protected BeanEditDialog(T bean, Class<T> type, BiFunction<Binder<T>, T, Optional<List<Component>>> fieldBinder, Predicate<T> submitHandler) {
        this.bean = bean;
        binder = new BeanValidationBinder<>(type);
        var fields = getFields(fieldBinder.apply(binder, bean));

        setCloseOnOutsideClick(false);
        setModal(true);
        createFormLayout(fields);
        createFooter();
        listenToEnterKeyForSubmit(fields);
        this.submitHandler = submitHandler;

        binder.setValidatorsDisabled(true);
        binder.readBean(bean);

        open();
        focusFirstBoundField();
    }

    private void focusFirstBoundField() {
        binder.getFields().flatMap(field ->
                        field instanceof Component component && component.isVisible() &&
                                field instanceof HasEnabled hasEnabled && hasEnabled.isEnabled() &&
                                field instanceof Focusable<?> focusable
                                ? Stream.of(focusable) : Stream.empty())
                .findFirst().ifPresent(Focusable::focus);
    }

    private void listenToEnterKeyForSubmit(Component[] fields) {
        Arrays.stream(fields)
                .filter(KeyNotifier.class::isInstance)
                .map(KeyNotifier.class::cast)
                .forEach(field -> field.addKeyPressListener(ENTER, e -> handleSubmitRequest()));
    }

    private void createFormLayout(Component[] fields) {
        FormLayout formLayout = new FormLayout(fields);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("350", 2));
        var numberOfColumns = Math.ceil(Arrays.stream(fields).filter(Component::isVisible).count() / 6.0);
        formLayout.setWidth((float) (350 * numberOfColumns), PIXELS);
        // make the first field double-width on two-column layout if number of fields is odd
        if (numberOfColumns == 2) {
            if (fields.length % 2 == 1) {
                formLayout.setColspan(fields[0], 2);
            }
        }
        add(formLayout);
    }

    private Component[] getFields(Optional<List<Component>> optionalComponents) {
        return optionalComponents
                .map(components -> components.toArray(Component[]::new))
                .orElseGet(() -> binder.getFields()
                        .filter(Component.class::isInstance)
                        .map(Component.class::cast)
                        .toArray(Component[]::new));
    }

    private void createFooter() {
        Button cancelButton = new Button(I18n.BEANEDITOR_FORM_BUTTON_CANCEL.get(), VaadinIcon.CLOSE_CIRCLE_O.create(), e -> close());
        Button submitButton = new Button(I18n.BEANEDITOR_FORM_BUTTON_SAVE.get(), VaadinIcon.CHECK.create(), e -> handleSubmitRequest());
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        getFooter().add(cancelButton);
        getFooter().add(submitButton);
    }

    private void handleSubmitRequest() {
        binder.setValidatorsDisabled(false);
        binder.getFields()
                .filter(HasValueChangeMode.class::isInstance)
                .map(HasValueChangeMode.class::cast)
                .forEach(field -> field.setValueChangeMode(EAGER)); //to immediately update validation status
        if (binder.writeBeanIfValid(bean)) {
            close();
            if (!submitHandler.test(bean)) { //close and open so that notifications can be shown in between (cannot be shown while a modal dialog is open due to a bug)
                open();
            }
        }
    }
}
