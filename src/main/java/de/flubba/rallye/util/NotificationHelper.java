package de.flubba.rallye.util;

import com.vaadin.flow.component.notification.Notification;
import lombok.extern.slf4j.Slf4j;

import static com.vaadin.flow.component.notification.Notification.Position.TOP_CENTER;
import static com.vaadin.flow.component.notification.NotificationVariant.LUMO_ERROR;
import static com.vaadin.flow.component.notification.NotificationVariant.LUMO_SUCCESS;

@Slf4j
public class NotificationHelper {

    private NotificationHelper() {
        // Utility class
    }

    public static void showAndLogError(String message) {
        Notification notification = new Notification(message);
        notification.setPosition(TOP_CENTER);
        notification.setDuration(5000);
        notification.addThemeVariants(LUMO_ERROR);
        notification.open();
        log.error(message);
    }

    public static void showSuccess(String message) {
        Notification notification = new Notification(message);
        notification.setPosition(TOP_CENTER);
        notification.setDuration(3000);
        notification.addThemeVariants(LUMO_SUCCESS);
        notification.open();
    }
}
