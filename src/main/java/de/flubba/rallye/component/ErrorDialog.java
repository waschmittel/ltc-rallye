package de.flubba.rallye.component;

import static de.flubba.rallye.component.MessageBox.ButtonId.OK;
import static de.flubba.rallye.component.MessageBox.MessageType.ERROR;

public class ErrorDialog {

    public ErrorDialog(String errorMessage) {
        MessageBox messageBox = new MessageBox(ERROR, errorMessage, "tiiiiitle", OK); //TODO: replace or remove title
        messageBox.getButton(OK).focus();
    }

}
