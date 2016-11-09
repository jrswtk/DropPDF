package pdf.drop.files;

public class FilePath {

    public static final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir")
            + "/DropPDF";
			
    public static final String TEMP_MERGE_PDF = FilePath.TEMP_DIRECTORY 
			+ "/merge.pdf";
			
    public static final String TEMP_CONVERT_PDF = FilePath.TEMP_DIRECTORY 
			+ "/convert.pdf";

}
