/*
 * Bluetooth communication for ECOsense device
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
 * Bluetooth communication for ECOsense device
 * @author pszjmb
 */
public class ECOsense extends RFCOMMClient{
    // ripped from sensaris decompile
    private static double aO3 = 0.77D;
    private static double bO3 = 1.367D;

    private static double aRhO3 = -0.031D;
    private static double bRhO3 = 0.165D;

    private static double aTO3 = -0.152D;
    private static double bTO3 = 0.3664D;

    private static double RlO3 = 300000.0D;

    private static double alphaTNOx = 707.0D;
    private static double betaTNOx = -2.03D;

    private static double RlNOx = 30000.0D;
    Float temp = null;
    Float hum = null;
    float value = -1;
    
    private Properties myProps;

    /**
     * Constructor
     * @param pIn is a set of Properties connecting the type of data with its storage table
     * @param dhIn is a DataHandler to transfer data to Timestreams
     */
    public ECOsense(Properties pIn,DataHandler dhIn) {
        super(pIn.getProperty("BT_ECOSense","null"), dhIn);
        if (!("null".equals(pIn.getProperty("BT_ECOSense", "null")))){
            myProps = pIn;
        }else{
            Logger.getLogger(TimeSense.class.getName()).log(
                Level.SEVERE, null, 
                "Unknown btspp for EcoSense. Make sure that the properties file has "
                    + "an entry like: "
                    + "<entry key=\"BT_ECOSense\">btspp://000780441BF2:1</entry>");     
            System.exit(1);
        }
    }
    
    @Override
    /**
     * Place data in the correct Timestream tables
     */
    public void processData(String data) {
        String[] columns = data.split(",");

        if (columns.length < 5) {
            return;
        }

        if ("$PSEN".equals(columns[1])) {
            if ("Batt".equals(columns[2])) {
                // min = 3.0, max = 4.5, volts
                //System.out.println(columns[2] + " " + columns[3] + " " + columns[4]);
                value = Float.parseFloat(columns[4]);
                //values.put("batt", value);

                //System.out.println("batt " + value);
                myHandler.execute(new String[]{myProps.getProperty("Batt", "null"),""+value});
            }
            if ("Noise".equals(columns[2])) {
                // min = 0, max = 140, db
                //System.out.println(columns[2] + " " + columns[3] + " " + columns[4]);
                value = Float.parseFloat(columns[4]);
                //System.out.println("noise " + value);
                myHandler.execute(new String[]{myProps.getProperty("Noise", "null"),""+value});
            }
            if ("NOx".equals(columns[2])) {
                // min = 0, max = 500, v?
                //System.out.println(columns[2] + " " + columns[3] + " " + columns[4]);
                double dvalue = Double.parseDouble(columns[4]);

                if (null == temp || null == hum) {
                    return;
                }

                // ripped from sensaris decompile
                double A2 = aRhO3 * hum + bRhO3;
                double B2 = aTO3 * temp + bTO3;
                double x = Math.log(RlO3 * dvalue) / (3.3D - dvalue);
                value = (float) Math.exp((x - A2 - B2 - bO3) / aO3);

                //System.out.println("nox " + value);
                myHandler.execute(new String[]{myProps.getProperty("NOx", "null"),""+value});
                

            }
            if ("COx".equals(columns[2])) {
                // min = 0, max = 500, ppm (not v)
                //System.out.println(columns[2] + " " + columns[3] + " " + columns[4]);
                value = Float.parseFloat(columns[4]);

                //System.out.println("cox " + value);
                myHandler.execute(new String[]{myProps.getProperty("COx", "null"),""+value});
            }
            if ("Hum".equals(columns[2])) {
                // min = 0, max = 100, %
                // temp min = -20, max = 120, F
                //System.out.println(columns[2] + " " + columns[3] + " " + columns[4] + " " + columns[5] + " " + columns[6]);
                hum = Float.parseFloat(columns[4]);
                temp = Float.parseFloat(columns[6]);

                //System.out.println("hum " + hum);
                myHandler.execute(new String[]{myProps.getProperty("Hum", "null"),""+hum});
                // System.out.println("temp " + temp);
                myHandler.execute(new String[]{myProps.getProperty("Temp", "null"),""+temp});

            }
        } else if ("$GPRMC".equals(columns[1])) {
            // This is the GPS. See http://sensing2010.blogspot.co.uk/p/device.html for format
        }
    }   
}
