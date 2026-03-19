package de.flubba.rallye.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import de.flubba.rallye.util.NotificationHelper;

import static com.vaadin.flow.component.Key.ENTER;

public class UnlockDialog extends Dialog {
    private final PasswordField passwordField = new PasswordField();
    private final String correctPassword;
    private final Runnable onUnlock;


    public UnlockDialog(String correctPassword, Runnable onUnlock) {
        this.correctPassword = correctPassword;
        this.onUnlock = onUnlock;
        passwordField.setWidthFull();
        passwordField.focus();
        passwordField.addKeyPressListener(ENTER, _ -> validateAndUnlock());

        VerticalLayout layout = new VerticalLayout(passwordField);
        layout.setPadding(true);

        Button unlockBtn = new Button("Unlock", VaadinIcon.UNLOCK.create(), _ -> validateAndUnlock());
        unlockBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", VaadinIcon.CLOSE_CIRCLE.create(), _ -> close());

        HorizontalLayout buttons = new HorizontalLayout(cancelButton, unlockBtn);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        getFooter().add(buttons);

        add(layout);
    }

    private void validateAndUnlock() {
        String password = passwordField.getValue();
        if (correctPassword.equals(password)) {
            onUnlock.run();
            close();
        } else {
            NotificationHelper.showAndLogError("Invalid password");
            passwordField.focus();
        }
    }
}
