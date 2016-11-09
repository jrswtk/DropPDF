package pdf.drop.alerts;

public interface Alert {

    javafx.scene.control.Alert.AlertType getAlertType();
    String getTitle();
    String getHeaderText();
    String getContentText();
	
}
