package pdf.drop.alerts;

import javafx.scene.control.Alert.AlertType;

public enum AlertEnum {

    FILE_WAS_SCALED(
            AlertType.INFORMATION,
            "Plik został scalny",
            "Wybrane pliki zostały scalone.",
            "Wygenerowany plik może zostać zapisany na dysku lub\nudostępniony przy pomocy konta Dropbox."),

    FILE_IS_NOT_UNAVAILABLE(
            AlertType.WARNING,
            "Brak dostępu do pliku",
            "Wymagany plik jest otwarty w innym programie.",
            "Zamknij program, który wykorzystuje wymagany plik \ni ponownie wybierz opcję."),

    FOLDER_CAN_NOT_BE_CREATED(
            AlertType.WARNING,
            "Folder nie może być utworzony.",
            "Folder nie może być utworzony.",
            "Aby utworzyć nowy folder wprowadz inną nazwę."),

    HELP_DROPPDF(
            AlertType.INFORMATION,
            "DropPDF",
            "Aplikacja umożliwia scalanie plików do formatu pdf.",
            "Aplikacja umożliwia scalenie plików w formatach: gif, jpg, png, pdf, docx do formatu pdf. " +
                    "Przy pomocy aplikacji scalony plik może zostać zapisany na dysku lub udostępniony " +
                    "za pośrednictwem konta Dropbox"),

    CONF_FILE_IS_NOT_UNAVAILABLE(
            AlertType.WARNING,
            "Plik konfiguracyjny",
            "Nie znaleziono pliku konfiguracyjnego.",
            "Przeprowadź autoryzacje lub zaimportuj plik z konfiguracją konta Dropbox."),

    DROPBOX_IS_DISCONNECT(
            AlertType.WARNING,
            "Brak połączenia z kontem Dropbox",
            "Brak połączenia z kontem Dropbox.",
            "Połącz aplikację z kontem Dropbox, aby udostępniać scalone pliki."),

    FILE_CAN_NOT_BE_SENT(
            AlertType.WARNING,
            "Plik nie może zostać wysłany",
            "Plik nie może zostać wysłany.",
            "W celu zapisania pliku w pierwszej kolejności należy scalić dokumenty."),

    FILE_CAN_NOT_BE_SAVE(
            AlertType.WARNING,
            "Plik nie może zostać zapisany",
            "Plik nie może zostać zapisany.",
            "W celu zapisania pliku w pierwszej kolejności należy scalić dokumenty."),

    FILES_CAN_NOT_BE_MERGE(
            AlertType.WARNING,
            "Pliki nie mogą zostać scalone",
            "Pliki nie mogą zostać scalone.",
            "W celu scalenia, należy dodać wybrane pliki do tabeli."),

    LINK_CAN_NOT_BE_GENERATED(
            AlertType.WARNING,
            "Link nie może zostać wygenerowany",
            "Link nie może zostać wygenerowany.",
            "W celu wygenerowania linku, należy scalić pliki oraz wysłać na konto " +
                    " dropbox scalony plik."),

    LINK_CAN_NOT_BE_COPIED(
            AlertType.WARNING,
            "Link nie może być skopiowany",
            "Link nie może być skopiowany.",
            "Aby skopiować link, należy wysłać na konto dropbox scalony plik oraz " +
                    "wygenerować do niego łącze."),

    FILE_WAS_SENT(
            AlertType.WARNING,
            "Plik został wysłany",
            "Scalony plik został umieszczony na dysku Dropbox.",
            ""),

    FILENAME_CAN_NOT_BE_SENT(
            AlertType.WARNING,
            "Plik nie może zostać wysłany",
            "Scalony plik nie może zostać wysłany.",
            "Wprowadź inną nazwę pliku, aby zapisać wymagany plik."),

    FILENAME_IS_INVALID(
            AlertType.WARNING,
            "Plik nie może zostać wysłany",
            "Nazwa pliku jest nieprawidłowa.",
            "Wprowadź inną nazwę pliku, aby zapisać wymagany plik."),

    CONF_FILE_WAS_CREATED(
            AlertType.INFORMATION,
            "Plik konfiguracyjny",
            "Utworzono plik konfiguracyjny.",
            "Aplikacja została połączona z kontem Dropbox."),

    HELP_AUTH(
            AlertType.INFORMATION,
            "Autoryzacja konta",
            "Autoryzacja umożliwia przypisanie do aplikacji konta Dropbox.",
            "Autoryzacja pozwala na wygenerowanie ustawień, które pozwolą na " +
                    "eksportowanie scalonych plików pdf, do określonego przez " +
                    "użytkownika miejsca na koncie Dropbox."),

    HELP_SENT_FILE(
            AlertType.INFORMATION,
            "Wysyłanie pliku",
            "Przy pomocy menedżera plików, można wysłać scalony plik pdf.",
            "Za pośrednictwem menedżera plików można wyeksportować scalony plik do " +
                    "określonego miejsca na koncie Dropbox.");

    private AlertType type;
    private String title;
    private String headerText;
    private String contextText;

    private AlertEnum(AlertType type, String title,
                      String headerText, String contextText) {

        this.type = type;
        this.title = title;
        this.headerText = headerText;
        this.contextText = contextText;
    }

    public AlertType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getHeaderText() {
        return headerText;
    }

    public String getContextText() {
        return contextText;
    }
}
