/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdf.drop.merge;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import java.io.File;
import java.io.IOException;
import javafx.concurrent.Task;
import pdf.drop.alerts.AlertEnum;
import pdf.drop.application.AppAlert;
import pdf.drop.converter.Converter;
import pdf.drop.data.DataFile;
import pdf.drop.data.DataFileFormat;
import pdf.drop.data.DataFileSource;

public class MergePerformer extends Task<File> {

    private MergeSortType sortType;
    private DataFileSource source;
    private MergeFiles mergeFiles;
    private Converter converter;
    
    private boolean isSuccess = true;

    public MergePerformer(DataFileSource source, MergeOrientation orientation,
            Rectangle pdfSize, MergeSortType sortType) throws IOException, DocumentException {
        
        this.source = source;
        this.mergeFiles = new MergeFiles(orientation, pdfSize);
        this.converter = new Converter(orientation);
        this.sortType = sortType;
    }

    @Override
    protected File call() throws InterruptedException {
        File file = null;
        try {
            file = perform();
        } catch (IOException | DocumentException ex) {
            AppAlert.showAndWaitAlert(AlertEnum.FILE_IS_NOT_UNAVAILABLE);
            isSuccess = false;
        }
        return file;
    }
    
    @Override
    protected void succeeded() {
        AppAlert.showAndWaitAlert(AlertEnum.FILE_WAS_SCALED);
    }

    public File perform() throws IOException, BadElementException, DocumentException {
        
        double startFile = 1;
        double countFiles = (double) source.size();
        
        for(DataFile dataFile : source.getDataFiles(sortType).keySet()) {

            DataFileFormat format = DataFileFormat.getFormat(dataFile.getFormat());
            File file = source.getFile(dataFile);
            
            switch (format) {
                case PDF: mergeFiles.add(file);
                    break;
                case DOCX: {
                    converter.startConvert();
                    File convertedFile = converter.convertDOCX(file);
                    converter.doneConvert();
                    mergeFiles.add(convertedFile);
                }
                    break;
                case JPG:
                case PNG:
                case GIF: {
                    Image image = Image.getInstance(file.getPath());
                    mergeFiles.add(image);
                }
                    break;
            }
            
            updateProgress(startFile, countFiles);
            startFile++;
        }
        
        return mergeFiles.save();
    }

}
