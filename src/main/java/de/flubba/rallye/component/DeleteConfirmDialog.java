package de.flubba.rallye.component;

import static de.flubba.rallye.component.MessageBox.ButtonId.CANCEL;
import static de.flubba.rallye.component.MessageBox.ButtonId.DELETE;
import static de.flubba.rallye.component.MessageBox.MessageType.QUESTION;

public class DeleteConfirmDialog {
    @FunctionalInterface
    public interface ConfirmButtonHandler {
        void onConfirm();
    }

    @FunctionalInterface
    public interface CancelButtonHandler {
        void onCancel();
    }

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
    public DeleteConfirmDialog(String question, String confirmCaption, String cancelCaption,
                               final ConfirmButtonHandler confirmButtonHandler, final CancelButtonHandler cancelButtonHandler) {
        MessageBox messageBox = new MessageBox(QUESTION, question, DELETE, CANCEL);
        messageBox.getButton(CANCEL).focus();
        messageBox.getButton(DELETE).setText(confirmCaption);
        messageBox.getButton(DELETE).addClickListener(event -> confirmButtonHandler.onConfirm());
        messageBox.getButton(CANCEL).setText(cancelCaption);
        if (cancelButtonHandler != null) {
            messageBox.getButton(CANCEL).addClickListener(event -> cancelButtonHandler.onCancel());
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
    public DeleteConfirmDialog(String question, String confirmCaption, String cancelCaption,
                               final ConfirmButtonHandler confirmButtonHandler) {
        this(question, confirmCaption, cancelCaption, confirmButtonHandler, null);
    }

}
