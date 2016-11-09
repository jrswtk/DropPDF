/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdf.drop.data;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javafx.collections.ObservableList;
import pdf.drop.merge.MergeSortType;

public class DataFileSource {

    private final Map<DataFile, File> dataFiles;

    public DataFileSource() {
        this.dataFiles = new HashMap<>();
    }

    public void addDataFile(DataFile dataFile, File file) {
        dataFiles.put(dataFile, file);
    }

    public Map<DataFile, File> getDataFiles(MergeSortType sortType) {

        List<Map.Entry<DataFile, File>> list
                = new LinkedList<>(dataFiles.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<DataFile, File>>() {
            public int compare(Map.Entry<DataFile, File> o1,
                    Map.Entry<DataFile, File> o2) {
                return (o1.getKey().getName()).compareTo(
                        o2.getKey().getName());
            }
        });
        
        if(sortType == MergeSortType.NAME_DESC) {
        	Collections.reverse(list);
        }

        Map<DataFile, File> sortedMap = new LinkedHashMap<>();
        for (Iterator<Map.Entry<DataFile, File>> it = list.iterator(); it.hasNext();) {
            Map.Entry<DataFile, File> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public Map<DataFile, File> getDataFiles(ObservableList<DataFile> data) {
        if (data.isEmpty()) {
            return dataFiles;
        }
        Map<DataFile, File> selectedData = new HashMap<>();
        for (DataFile dataTable : data) {
            selectedData.put(dataTable, dataFiles.get(dataTable));
        }
        return selectedData;
    }

    public File getEqualFile(DataFile dataFile) {
        for (DataFile df : dataFiles.keySet()) {
            if (df.equals(dataFile)) {
                return dataFiles.get(df);
            }
        }
        return null;
    }

    public void createSource(DataFileSource notEmpty, List<DataFile> datas) {
        for (DataFile dataFile : datas) {
            dataFiles.put(dataFile, notEmpty.getFile(dataFile));
        }
    }

    public File getFile(DataFile dataFile) {
        return dataFiles.get(dataFile);
    }

    public void remove(DataFile dataFile) {
        dataFiles.remove(dataFile);
    }

    public void remove(List<DataFile> toRemove) {
        for (DataFile dataFile : toRemove) {
            if (dataFiles.containsKey(dataFile)) {
                dataFiles.remove(dataFile);
            }
        }
    }

    public void clear() {
        dataFiles.clear();
    }

    public int size() {
        return dataFiles.size();
    }

}
