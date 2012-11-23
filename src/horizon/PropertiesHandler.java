/*
 * Handles setting up a properties file
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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles setting up a properties file
 * @author pszjmb
 */
public class PropertiesHandler {

    public void writeProperties(String url) {
        Properties p = new Properties();
        p.setProperty("TimestreamsUrl", "http://timestreams.wp.horizon.ac.uk/wp-content/plugins/timestreams/2/measurements/");
        p.setProperty("proxyUrl", "wwwcache-20.cs.nott.ac.uk");
        p.setProperty("proxyPort", "3128");
        p.setProperty("username", "admin");
        p.setProperty("password", "Time349");
        p.setProperty("Batt", "wp_1_ts_Battery_72");
        p.setProperty("Noise", "wp_1_ts_noise_73");
        p.setProperty("NOx", "wp_1_ts_Nitrogen_Oxide_77");
        p.setProperty("COx", "wp_1_ts_cox_74");
        p.setProperty("Hum", "wp_1_ts_Humidity_75");
        p.setProperty("Temp", "wp_1_ts_Temperature_76");
        p.setProperty("CO2", "wp_1_ts_CO2_78");
        p.setProperty("BT_ECO2Sense", "btspp://00078046E0B4:1");
        p.setProperty("BT_ECOSense", "btspp://000780441BF2:1");
        
        OutputStream os = null;
        try {
            os = new FileOutputStream(url);
            p.storeToXML(os, "Properties for storing readings to Timestreams");
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if(null != os){
                    os.close();
                }
            } catch (IOException ex) {
                //ignore
            }
        }

    }
    
    public void testReadProperties(String url) {
        InputStream is = null;
        try {
            Properties p = new Properties();
            is = new FileInputStream(url);
            p.loadFromXML(is);
            System.out.println(p.toString());
            System.out.println("temp: " + p.getProperty("Temp"));
        } catch (IOException ex) {
            Logger.getLogger(PropertiesHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if(null != is){
                    is.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(PropertiesHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }

    public static void main(String args[]) {
        PropertiesHandler ph = new PropertiesHandler();
        ph.writeProperties(args[0]);
        ph.testReadProperties(args[0]);
    }
}
