package com.sgdata.mapping.client.tools;

import com.sgdata.mapping.client.beans.InfoMappingDTO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Francisco Ruiz
 */

public class DatabaseTools {
    static final Logger LOGGER = LogManager.getLogger(DatabaseTools.class.getName());
    
    private final String DB_DRIVER = "net.sourceforge.jtds.jdbc.Driver";
    
    private final String DB_URL = "jdbc:jtds:sqlserver://127.0.0.1:1433/Listas_Sat";
    private final String DB_USER = "reporter";
    private final String DB_PASS = "12121212qw";
    
    public InfoMappingDTO databaseFinder(InfoMappingDTO infoMapping) {
        String rfcFetched = null;
        
        try {
            Class.forName(DB_DRIVER);
        } catch(ClassNotFoundException e) {
            LOGGER.fatal("Class Not Found: " + e.getMessage());
        }
        
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            Statement statement = connection.createStatement();
            ResultSet resultSet;
            
            resultSet = statement.executeQuery("SELECT rfc FROM LCO_Primaria WHERE rfc = \'" + infoMapping.getRfc() + "\'");
            
            while(resultSet.next()) {
                rfcFetched = resultSet.getString("rfc");
            }
            
            resultSet.close();
            statement.close();
            connection.close();
            
            if (rfcFetched != null) {
                infoMapping.setLco(true);
                LOGGER.trace("Query Result: " + infoMapping.getRfc() + " - LCO: " + infoMapping.isLco());
            } else {
                infoMapping.setLco(false);
                LOGGER.trace("Query Result: " + infoMapping.getRfc() + " - LCO: " + infoMapping.isLco());
            }
            
        } catch(SQLException e) {
            LOGGER.fatal("SQL Exception: " + e.getMessage());
        }
        LOGGER.debug("Query Result: " + infoMapping.getRfc() + " - LCO: " + infoMapping.isLco());
        return infoMapping;
    }
}
