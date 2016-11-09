/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdf.drop.merge;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import pdf.drop.alerts.AlertEnum;
import pdf.drop.application.AppAlert;
import pdf.drop.application.AppFiles;
import pdf.drop.files.FilePath;

public class MergeFiles {

    private MergeOrientation orientation = MergeOrientation.HORIZONTAL;
    private FileOutputStream outputStream = null;
    private PdfContentByte content = null;
    private Document document = null;
    private PdfWriter writer = null;
    private File outputFile = null;



    public MergeFiles(MergeOrientation orientation, Rectangle pdfSize) throws IOException, DocumentException {
        AppFiles.createTempFiles();

        this.orientation = orientation;

        this.outputFile = new File(FilePath.TEMP_MERGE_PDF);

        switch (orientation) {
            case HORIZONTAL:
                this.document = new Document(pdfSize.rotate());
                break;
            case VERICAL:
                this.document = new Document(pdfSize);
                break;
        }

        try {
            this.outputStream = new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            AppAlert.showAndWaitAlert(AlertEnum.FILE_IS_NOT_UNAVAILABLE);
        }
        this.writer = PdfWriter.getInstance(document, outputStream);

        MergeRotate rotate = new MergeRotate();
        writer.setPageEvent(rotate);

        this.document.open();

        switch (orientation) {
            case HORIZONTAL:
                rotate.setRotation(PdfPage.LANDSCAPE);
                break;
            case VERICAL:
                rotate.setRotation(PdfPage.PORTRAIT);
        }

        this.content = writer.getDirectContent();
    }

    public void add(File pdfFile) throws IOException {
        PdfReader reader = new PdfReader(new FileInputStream(pdfFile));
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            document.newPage();
            PdfImportedPage page = writer.getImportedPage(reader, i);
            content.addTemplate(page, 0, 0);
        }
    }

    public void add(Image image) throws DocumentException {
        document.newPage();

        if(image.getWidth() > document.getPageSize().getWidth()
                || image.getHeight()> document.getPageSize().getHeight()) {

            image.scaleToFit(
                document.getPageSize().getWidth(),
                document.getPageSize().getHeight());
        }

        image.setAlignment(Image.ALIGN_CENTER);

        document.newPage();
        document.add(image);
    }

    public File save() throws IOException {
        outputStream.flush();
        document.close();
        outputStream.close();

        return outputFile;
    }

    public MergeOrientation getOrientation() {
        return orientation;
    }

}
