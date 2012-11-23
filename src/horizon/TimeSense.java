/*
 * Main runner application to load Bluetooth data from a Sensaris 
 * Sensepod into Timestreams
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
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main runner application to load Bluetooth data from a Sensaris 
 * Sensepod into Timestream
 * @author pszjmb
 */
public class TimeSense {

    /**
     * 
     * @param args[0] should be URL to properties XML file, args[2] should be 
     * type of sensor (either "ECOsense" or "ECO2sense")
     */
    public static void main(String args[]) {
        if (args.length != 2 || !(args[1].equals("ECOsense") || args[1].equals("ECO2sense"))) {
            System.err.println("Incorrect Parameters.\nTimeSense Usage:\n"
                    + "java -jar TimeSense.jar [URL to properties XML file] [ECOsense or ECO2sense]");
        }
        InputStream is = null;
        try {
            Properties p = new Properties();
            is = new FileInputStream(args[0]);
            p.loadFromXML(is);

            //btspp://000780441BF2:1 == Senspod 76
            DataHandler dh = new HttpReq();
            dh.init(p);
            if (args[1].equals("ECOsense")) {
                RFCOMMClient rcom = new ECOsense(p, dh);
                Thread t = new Thread(rcom);
                t.start();
            } else if (args[1].equals("ECO2sense")) {
                RFCOMMClient rcom = new ECO2sense(p, dh);
                Thread t = new Thread(rcom);
                t.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(TimeSense.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
