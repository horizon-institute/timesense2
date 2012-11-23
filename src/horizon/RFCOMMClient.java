/*
 * Handles Bluetooth communication
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

import java.io.*;
import javax.microedition.io.*;

/**
 * Abstract base class for handling Bluetooth communication
 * @author pszjmb
 */
public abstract class RFCOMMClient implements Runnable{

    private String btspp;       // bluetooth address in the form: btspp://000780441BF2:1
    private boolean running = true;
    protected DataHandler myHandler;

    public RFCOMMClient(String btspp, DataHandler dhIn) {
        this.btspp = btspp;
        myHandler = dhIn;
    }

    public String getBluetoothAddressspp() {
        return btspp;
    }

    /**
     * In the form btspp://000780441BF2:1
     * @param btspp 
     */
    public void setBluetoothAddress(String btspp) {
        this.btspp = btspp;
    }
        
    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
    
    @Override
    public void run(){
        try {
            StreamConnection conn = (StreamConnection) 
                    Connector.open(btspp);

            InputStream in = conn.openInputStream();
            BufferedReader inStream = new BufferedReader(new InputStreamReader(in));
            String data;

            while (running) {
                data = inStream.readLine();
                //System.out.println(data);
                processData(data);
            }
            myHandler.setRunning(false);
            conn.close();
        } catch (IOException e) {
            System.err.print(e.toString());
        }
    }

    public abstract void processData(String data);
}
