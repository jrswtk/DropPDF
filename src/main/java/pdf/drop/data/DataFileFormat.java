package pdf.drop.data;

public enum DataFileFormat {

    PDF("pdf", "*.pdf"),
    DOCX("docx", "*.docx"),
    JPG("jpg", "*.jpg"),
    PNG("png", "*.png"),
    GIF("gif", "*.gif");
    
    private final String suffix;
    private final String extension;

    private DataFileFormat(String suffix, String extension) {
        this.suffix = suffix;
        this.extension = extension;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getExtension() {
        return extension;
    }
    
    public static boolean compare(String string) {
        for(DataFileFormat format : values()) {
            if(format.getSuffix().equalsIgnoreCase(string)) {
                return true;
            }
        }
        return false;
    }
    
    public static DataFileFormat getFormat(String string) {
        for(DataFileFormat format : values()) {
            if(format.getSuffix().equalsIgnoreCase(string)) {
                return format;
            }
        }
        return null;
    }
    

}
