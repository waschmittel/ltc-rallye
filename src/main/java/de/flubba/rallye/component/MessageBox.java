package de.flubba.rallye.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;
import java.util.EnumMap;

public class MessageBox {
    @RequiredArgsConstructor
    public enum ButtonId {
        // the order of definition in the enum represents the order in which the
        // buttons will apprear
        OK(VaadinIcon.CHECK, ButtonVariant.LUMO_PRIMARY),
        YES(VaadinIcon.CHECK, ButtonVariant.LUMO_SUCCESS),
        NO(VaadinIcon.BAN, ButtonVariant.LUMO_ERROR),
        CANCEL(null, null);

        private final VaadinIcon icon;
        private final ButtonVariant style;
    }

    @RequiredArgsConstructor
    public enum MessageType {
        QUESTION(VaadinIcon.QUESTION_CIRCLE),
        INFO(VaadinIcon.INFO_CIRCLE),
        WARNING(VaadinIcon.EXCLAMATION_CIRCLE),
        ERROR(VaadinIcon.CLOSE_CIRCLE);

        private final VaadinIcon icon;
    }

    private final EnumMap<ButtonId, Button> buttons = new EnumMap<>(ButtonId.class);
    private final HorizontalLayout buttonLayout = new HorizontalLayout();
    private final Span messageLabel = new Span("");
    private final Div iconLabel = new Div();
    private final Dialog dialog = new Dialog();

    /**
     * create a message window that will close when any of the buttons is
     * clicked
     *
     * @param messageType type of message
     * @param message the message to show
     * @param buttonIds the types of buttons that should be displayed
     */
    public MessageBox(MessageType messageType, String message, ButtonId... buttonIds) {
        HorizontalLayout messageLayout = new HorizontalLayout();
        messageLayout.add(iconLabel);
        messageLayout.add(messageLabel);

        VerticalLayout layout = new VerticalLayout();
        layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        layout.add(messageLayout);
        layout.add(buttonLayout);

        buttonLayout.setSpacing(true);

        dialog.add(layout);
        dialog.setModal(true);

        initButtons(buttonIds);

        iconLabel.add(messageType.icon.create());
        messageLabel.setText(message);
        dialog.open();
    }

    private void initButtons(ButtonId... buttonIds) {
        for (ButtonId buttonId : buttonIds) {
            Button button = new Button();
            if (buttonId.icon != null) {
                button.setIcon(buttonId.icon.create());
            }
            if (buttonId.style != null) {
                button.addThemeVariants(buttonId.style);
            }
            button.addClickListener(event -> dialog.close());
            buttons.put(buttonId, button);
        }
        for (ButtonId buttonId : ButtonId.values()) {
            if (buttons.containsKey(buttonId)) {
                buttonLayout.add(buttons.get(buttonId));
            }
        }
    }

    public Button getButton(ButtonId buttonId) {
        if (!buttons.containsKey(buttonId)) {
            throw new RuntimeException(MessageFormat
                    .format("Button {0} war requested via getButton but not specified in constructor",
                            buttonId));
        }
        return buttons.get(buttonId);
    }

}
