package de.flubba.rallye.component;

import com.vaadin.flow.component.button.Button;

import static de.flubba.rallye.component.MessageBox.ButtonId.CANCEL;
import static de.flubba.rallye.component.MessageBox.ButtonId.YES;
import static de.flubba.rallye.component.MessageBox.MessageType.QUESTION;

public class ConfirmDialog { //TODO: maybe use Vaadin's ConfirmDialog
    @FunctionalInterface
    public interface ConfirmButtonHandler {
        void onConfirm();
    }

    @FunctionalInterface
    public interface CancelButtonHandler {
        void onCancel();
    }

    private final MessageBox messageBox;

    /**
     * show a confirmation dialog
     *
     * @param question the question to ask, putting text into a ContentMode.HTML
     * formatted label
     * @param confirmCaption the caption of the "cancel" button
     * @param cancelCaption the caption of the "cancel" button
     * @param confirmButtonHandler what should be done if the answer is "yes"
     * @param cancelButtonHandler what should be done if the answer is "no"
     */
    public ConfirmDialog(String question, String confirmCaption, String cancelCaption,
                         final ConfirmButtonHandler confirmButtonHandler, final CancelButtonHandler cancelButtonHandler) {
        messageBox = new MessageBox(QUESTION, question, null, YES, CANCEL);
        messageBox.getButton(CANCEL).focus();
        messageBox.getButton(YES).setText(confirmCaption);
        messageBox.getButton(YES).addClickListener(event -> confirmButtonHandler.onConfirm());
        messageBox.getButton(CANCEL).setText(cancelCaption);
        if (cancelButtonHandler != null) {
            messageBox.getButton(YES).addClickListener(event -> cancelButtonHandler.onCancel());
        }
    }

    /**
     * show a confirmation dialog
     *
     * @param question the question to ask, putting text into a ContentMode.HTML
     * formatted label
     * @param confirmCaption the caption of the "cancel" button
     * @param cancelCaption the caption of the "cancel" button
     * @param confirmButtonHandler what should be done if the answer is "yes"
     */
    public ConfirmDialog(String question, String confirmCaption, String cancelCaption,
                         final ConfirmButtonHandler confirmButtonHandler) {
        this(question, confirmCaption, cancelCaption, confirmButtonHandler, null);
    }

    /**
     * get the "no" button (use to style the button or give it an icon)
     *
     * @return the "no" button
     */
    public Button getCancelButton() {
        return messageBox.getButton(CANCEL);
    }

    /**
     * get the "yes" button (use to style the button or give it an icon)
     *
     * @return the "yes" button
     */
    public Button getConfirmButton() {
        return messageBox.getButton(YES);
    }

}
