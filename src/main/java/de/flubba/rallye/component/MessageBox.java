package de.flubba.rallye.component;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;
import java.util.HashMap;

public class MessageBox {
    @RequiredArgsConstructor
    public enum ButtonId {
        // the order of definition in the enum represents the order in which the
        // buttons will apprear
        OK(VaadinIcon.CHECK, ButtonVariant.LUMO_PRIMARY),
        YES(VaadinIcon.CHECK, ButtonVariant.LUMO_SUCCESS), //TODO: does this fit FRIENDLY?
        NO(VaadinIcon.BAN, ButtonVariant.LUMO_ERROR), //TODO: does this fit DANGER?
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

    private static final String ICON_CLASS = "message-box-icon";
    private static final String LABEL_CLASS = "message-box-label";

    private final HashMap<ButtonId, Button> buttons = new HashMap<>();
    private final HorizontalLayout buttonLayout = new HorizontalLayout();
    private final Text messageLabel = new Text("");
    private final Div iconLabel = new Div(); //TODO: is this the right component for this? also see above
    private final Dialog window = new Dialog();

    {
        /* TODO iconLabel.setContentMode(ContentMode.HTML);
        iconLabel.addStyleName(ICON_CLASS);
        messageLabel.addStyleName(LABEL_CLASS);
        messageLabel.setContentMode(ContentMode.HTML);*/

        HorizontalLayout messageLayout = new HorizontalLayout();
        messageLayout.add(iconLabel);
        messageLayout.add(messageLabel);
        /* TODO messageLayout.setComponentAlignment(iconLabel, Alignment.MIDDLE_CENTER);
        messageLayout.setComponentAlignment(messageLabel, Alignment.MIDDLE_LEFT);
        messageLayout.setExpandRatio(messageLabel, 1);*/

        VerticalLayout layout = new VerticalLayout();
        /* TODO layout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        layout.setMargin(true);
        layout.setSpacing(true);*/
        layout.add(messageLayout);
        layout.add(buttonLayout);

        buttonLayout.setSpacing(true);

        window.add(layout); //TODO: is this OK?
        window.setModal(true);
        window.setCloseOnEsc(false);
        window.setCloseOnOutsideClick(false);
        window.setResizable(false);
    }

    /**
     * create a message window that will close when any of the buttons is
     * clicked
     *
     * @param messageType type of message
     * @param message the message to show
     * @param title the title of the message window - may me null
     * @param buttonIds the types of buttons that should be displayed
     */
    public MessageBox(MessageType messageType, String message, String title, ButtonId... buttonIds) {
        initButtons(buttonIds);
        iconLabel.add(messageType.icon.create());
        // TODO: find replacement for iconLabel.addStyleName(messageType.getCssClass());
        messageLabel.setText(message); //TODO: this should probably wrap
        //TODO window.setCaption(title);
        window.open();
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
            button.addClickListener(event -> window.close());
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
