package pdf.drop.application;

import pdf.drop.alerts.Alert;
import pdf.drop.alerts.AlertEnum;
import pdf.drop.alerts.AlertImpl;

public class AppAlert {

    public static javafx.scene.control.Alert showAndWaitAlert(AlertEnum alertEnum) {
        Alert alert = new AlertImpl(alertEnum);

        return showAndWaitAlert(alert);
    }

    public static javafx.scene.control.Alert showAndWaitAlert(pdf.drop.alerts.Alert alert) {

        javafx.scene.control.Alert guiAlert = showAndWaitAlert(
                alert.getAlertType(),
                alert.getTitle(),
                alert.getHeaderText(),
                alert.getContentText());

        return guiAlert;
    }

    public static javafx.scene.control.Alert showAndWaitAlert(
            javafx.scene.control.Alert.AlertType alertType,
                                         String title,
                                         String headerText,
                                         String contactText) {

        javafx.scene.control.Alert alert =
                new javafx.scene.control.Alert(alertType);

        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contactText);
        alert.showAndWait();

        return alert;
    }

}
