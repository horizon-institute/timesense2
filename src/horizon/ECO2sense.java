/*
 * Bluetooth communication for ECO2sense device
 * Copyright (C) 2012 Jesse Blum (pszjmb | JMB), Horizon Digital Economy Institute, University of Nottingham
 * This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package horizon;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bluetooth communication for ECO2sense device
 * @author pszjmb
 */
public class ECO2sense extends RFCOMMClient{
    String dbTable;
    Properties myProps;
    /**
     * Constructor
     * @param pIn is a set of Properties connecting the type of data with its storage table
     * @param dhIn is a DataHandler to transfer data to Timestreams
     */
    public ECO2sense(Properties pIn, DataHandler dhIn) {
        super(pIn.getProperty("BT_ECO2Sense","null"), dhIn);
        if (!("null".equals(pIn.getProperty("BT_ECO2Sense", "null")))){
            myProps = pIn;
        }else{
            Logger.getLogger(TimeSense.class.getName()).log(
                Level.SEVERE, null, 
                "Unknown btspp for EcoSense. Make sure that the properties file has "
                    + "an entry like: "
                    + "<entry key=\"BT_ECOSense\">btspp://000780441BF2:1</entry>");     
            System.exit(1);
        }
        if (!("null".equals(pIn.getProperty("CO2", "null")))){
            dbTable = pIn.getProperty("CO2");
        }else{
            Logger.getLogger(TimeSense.class.getName()).log(
                Level.SEVERE, null, 
                "Unknown measurement container for Eco2Sense. Make sure that the properties file has "
                    + "an entry like: "
                    + "<entry key=\"CO2\">wp_1_ts_CO2_78:1</entry>");     
            System.exit(1);
        }
    }
    
    @Override
    /**
     * Place CO2 data in the correct Timestream table
     */
    public void processData(String data) {
        String[] columns = data.split(",");
        float value=-1;

        if (columns.length < 5) {
            return;
        }

        if ("$PSEN".equals(columns[1])) {
            if ("CO2".equals(columns[2])) {
                // ppm (not v)
                value = Float.parseFloat(columns[4]);
                //System.out.println("CO2 " + value);
                myHandler.execute(new String[]{dbTable,""+value});
            }
        } else if ("$GPRMC".equals(columns[1])) {
            // This is the GPS. See http://sensing2010.blogspot.co.uk/p/device.html for format
        }
    }    
}