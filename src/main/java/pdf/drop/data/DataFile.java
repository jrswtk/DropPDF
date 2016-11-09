package pdf.drop.data;

import javafx.beans.property.SimpleStringProperty;

public final class DataFile {

    private final SimpleStringProperty name;
    private final SimpleStringProperty date;
    private final SimpleStringProperty format;
    private final SimpleStringProperty size;
   
    public DataFile(String name, String date, String format, String size) {
        this.name = new SimpleStringProperty(name);
        this.date = new SimpleStringProperty(date);
        this.format = new SimpleStringProperty(format);
        this.size = new SimpleStringProperty(size);
    }

    public String getName() {
        return this.name.get();
    }

    public String getDate() {
        return this.date.get();
    }

    public String getFormat() {
        return this.format.get();
    }

    public String getSize() {
        return this.size.get();
    }

    @Override
    public String toString() {
        return this.name.get() + "." + this.getFormat();
    }
    
}
