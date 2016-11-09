/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdf.drop.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;
import pdf.drop.files.FilePath;
import pdf.drop.merge.MergeOrientation;

public class Converter {

    private File outFile;
    private MergeOrientation orientation;
    private FileOutputStream outputStream;

    public Converter(MergeOrientation orientation) throws FileNotFoundException {
        this.outFile = new File(FilePath.TEMP_CONVERT_PDF);
        this.orientation = orientation;
    }

    public File convertDOCX(File fileDOCX) throws FileNotFoundException, IOException {
        FileInputStream inputStream = new FileInputStream(fileDOCX);
        XWPFDocument document = new XWPFDocument(inputStream);

        switch (orientation) {
            case HORIZONTAL:
                document.getDocument().getBody().getSectPr().getPgSz()
                        .setOrient(STPageOrientation.LANDSCAPE);
                break;
				
            case VERICAL:
                document.getDocument().getBody().getSectPr().getPgSz()
                        .setOrient(STPageOrientation.PORTRAIT);
                break;
        }

        PdfOptions options = PdfOptions.create();
        PdfConverter.getInstance().convert(document, outputStream, options);

        inputStream.close();

        return outFile;
    }

    public void startConvert() throws FileNotFoundException {
        this.outputStream = new FileOutputStream(getOutFile());
    }

    public void doneConvert() throws IOException {
        outputStream.close();
    }

    public File getOutFile() {
        return outFile;
    }

}
