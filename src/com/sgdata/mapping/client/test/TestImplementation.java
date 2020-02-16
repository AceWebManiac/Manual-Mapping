package com.sgdata.mapping.client.test;

import com.sgdata.mapping.client.beans.InfoMappingDTO;
import com.sgdata.mapping.client.tools.ApachePOITools;
import com.sgdata.mapping.client.tools.DatabaseTools;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Francisco Ruiz
 */

public class TestImplementation {
    static final Logger LOGGER = LogManager.getLogger(TestImplementation.class.getName());

    private static final String SOURCE_FILE = "./documents/REPUESTA_PMA120507MH2_09012020_2_AV202063375742.xlsx";
    
    public static void main(String[] args) {
        LOGGER.info("Starting Execution ...");
        long startTime = System.nanoTime();
        try {
            ApachePOITools poiTools = new ApachePOITools();
            List<InfoMappingDTO> infoMappingList = poiTools.readMappingTemplate(SOURCE_FILE);
            LOGGER.info("Fetched Registries: " + infoMappingList.size());
            DatabaseTools dataTools = new DatabaseTools();
            List<InfoMappingDTO> infoMappingResult = new ArrayList<>();
            for (int i = 0; i < infoMappingList.size(); i++) {
                InfoMappingDTO infoMapping = dataTools.databaseFinder(infoMappingList.get(i));
                infoMappingResult.add(infoMapping);
            }
            poiTools.writeMappingTemplate(infoMappingResult);
            long finishTime = System.nanoTime();
            long timeElapsed = finishTime - startTime;
            LOGGER.info("Execution time in Miliseconds: " + timeElapsed / 1000000);
            LOGGER.info("Finalizing Execution ...");
        } catch(Exception e) {
            LOGGER.fatal("Error Catched: " + e.getMessage());
            e.getMessage();
        }
    }
}
