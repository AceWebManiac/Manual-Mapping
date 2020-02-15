package com.sgdata.mapping.client.tools;

import com.sgdata.mapping.client.beans.InfoMappingDTO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Francisco Ruiz
 */

public class ApachePOITools {
    static final Logger LOGGER = LogManager.getLogger(ApachePOITools.class.getName());
    
    static final String RFC_PATTERN = "[A-Z,Ñ,&amp;]{3,4}[0-9]{2}[0-1][0-9][0-3][0-9][A-Z,0-9][A-Z,0-9][0-9,A-Z]";
    static final String CURP_PATTERN = "[A-Z][A,E,I,O,U,X][A-Z]{2}[0-9]{2}[0-1][0-9][0-3][0-9][M,H][A-Z]{2}[B,C,D,F,G,H,J,K,L,M,N,Ñ,P,Q,R,S,T,V,W,X,Y,Z]{3}[0-9,A-Z][0-9]";
    
    public List<InfoMappingDTO> readMappingTemplate(String templateFilePath) {
        LOGGER.trace("Entering readMappingTemplate ...");
        List<InfoMappingDTO> infoListMapping = new ArrayList<>();
        try {
            FileInputStream source = new FileInputStream(new File(templateFilePath));
            
            Workbook workbook = new XSSFWorkbook(source);
            Sheet sheet = workbook.getSheetAt(0);
            LOGGER.info("Total rows to read: " + sheet.getLastRowNum());
            
            Iterator<Row> rowIterator = sheet.iterator();
            
            for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
                sheet.removeMergedRegion(i);
            }
            
            while(rowIterator.hasNext()) {
                Row currentRow = rowIterator.next();
                Iterator<Cell> cellIterator = currentRow.cellIterator();
                LOGGER.debug("Reading Row: " + currentRow.getRowNum());
                
                if (currentRow.getRowNum() <= sheet.getLastRowNum()) {
                    InfoMappingDTO infoMapping = new InfoMappingDTO();
                    
                    while(cellIterator.hasNext()) {
                        Cell currentCell = cellIterator.next();
                        int columnIndex = currentCell.getColumnIndex();
                        LOGGER.debug("Reading Column: " + columnIndex);
                        
                        if (currentCell.getCellType() != CellType.BLANK) {
                            switch(columnIndex) {
                                case 1:
                                    Pattern curpPattern = Pattern.compile(CURP_PATTERN);
                                    Matcher curpMatcher = curpPattern.matcher(getCellValue(currentCell).toString());
                                    if (curpMatcher.matches())
                                        infoMapping.setCurp(getCellValue(currentCell).toString());
                                    break;
                                case 2:
                                    Pattern rfcPattern = Pattern.compile(RFC_PATTERN);
                                    Matcher rfcMatcher = rfcPattern.matcher(getCellValue(currentCell).toString());
                                    if (rfcMatcher.matches())
                                        infoMapping.setRfc(getCellValue(currentCell).toString());
                                    break;
                            }
                        }
                    }
                    if (infoMapping.getCurp() != null && infoMapping.getRfc() != null)
                    infoListMapping.add(infoMapping);
                }
            }
            
        } catch(IOException e) {
            LOGGER.fatal("Read Mapping Template: " + e.getMessage());
        }
        LOGGER.info("Current Readed Registries: " + infoListMapping.size());
        LOGGER.trace("Exiting readMappingTemplate ...");
        return infoListMapping;
    }
    
    public void writeMappingTemplate(List<InfoMappingDTO> infoListMapping) {
        LOGGER.trace("Entering writeMappingTemplate ...");
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        
        int rowCount = 0;
        
        for (InfoMappingDTO infoMapping: infoListMapping) {
            Row currentRow = sheet.createRow(rowCount++);
            LOGGER.debug("Reading Row: " + currentRow.getRowNum());
            writeInfoMapping(infoMapping, currentRow);
        }
        
        try (FileOutputStream outputStream = new FileOutputStream("./documents/Extracted Data File.xlsx")) {
            LOGGER.trace("Entering Workbook Writting ...");
            workbook.write(outputStream);
            LOGGER.trace("Exiting Workbook Writting ...");
        } catch(IOException e) {
            LOGGER.fatal("Write Mapping Template: " + e.getMessage());
        }
        
        LOGGER.trace("Exiting writeMappingTemplate ...");
    }
    
    private void writeInfoMapping(InfoMappingDTO infoMapping, Row currentRow) {
        LOGGER.trace("Entering writeInfoMapping ...");
        Cell cell = currentRow.createCell(0);
        LOGGER.debug("CURP Readed: " + infoMapping.getCurp());
        cell.setCellValue(infoMapping.getCurp());
        LOGGER.debug("CURP Writed: " + infoMapping.getCurp());
        
        cell = currentRow.createCell(1);
        LOGGER.debug("RFC Readed: " + infoMapping.getRfc());
        cell.setCellValue(infoMapping.getRfc());
        LOGGER.debug("RFC Writed: " + infoMapping.getRfc());
        
        cell = currentRow.createCell(2);
        LOGGER.debug("isLCO Readed: " + infoMapping.isLco());
        cell.setCellValue(infoMapping.isLco());
        LOGGER.debug("isLCO Writed: " + infoMapping.isLco());
        
        LOGGER.info("Writted Register CURP: " + infoMapping.getCurp() + " - RFC: " + infoMapping.getRfc() + " - isLco: " + infoMapping.isLco());
        
        LOGGER.trace("Exiting writeInfoMapping ...");
    }
    
    private Object getCellValue(Cell currentCell) {
        LOGGER.trace("Entering getCellValue ...");
        switch(currentCell.getCellType()) {
            case STRING:
                LOGGER.trace("Entering String Cell Value");
                LOGGER.debug("String Cell Content: " + currentCell.getStringCellValue());
                LOGGER.trace("Exiting String Cell Value");
                return currentCell.getStringCellValue();
            case NUMERIC:
                LOGGER.trace("Entering Numeric Cell Value");
                LOGGER.debug("Numeric Cell Content: " + currentCell.getNumericCellValue());
                LOGGER.trace("Exiting Numeric Cell Value");
                return currentCell.getNumericCellValue();
            case FORMULA:
                LOGGER.trace("Entering Formula Cell Value");
                LOGGER.trace("Formula Type: " + currentCell.getCachedFormulaResultType());
                switch(currentCell.getCachedFormulaResultType()) {
                    case STRING:
                        LOGGER.trace("Entering String Cell Value");
                        LOGGER.debug("String Cell Content: " + currentCell.getStringCellValue());
                        LOGGER.trace("Exiting String Cell Value");
                        return currentCell.getStringCellValue();
                    case NUMERIC:
                        LOGGER.trace("Entering Numeric Cell Value");
                        LOGGER.debug("Numeric Cell Content: " + currentCell.getNumericCellValue());
                        LOGGER.trace("Exiting Numeric Cell Value");
                        return currentCell.getNumericCellValue();
                }
                LOGGER.trace("Exiting Formula Cell Value");
        }
        LOGGER.trace("Exiting getCellValue ...");
        return null;
    }
}
