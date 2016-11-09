package pdf.drop.alerts;

import javafx.scene.control.Alert.AlertType;

public class AlertImpl implements pdf.drop.alerts.Alert {

    private AlertEnum alertEnum;

    public AlertImpl(AlertEnum alertEnum) {
        this.alertEnum = alertEnum;
    }

    @Override
    public AlertType getAlertType() {
        return alertEnum.getType();
    }

    @Override
    public String getTitle() {
        return alertEnum.getTitle();
    }

    @Override
    public String getHeaderText() {
        return alertEnum.getHeaderText();
    }

    @Override
    public String getContentText() {
        return alertEnum.getContextText();
    }
}
