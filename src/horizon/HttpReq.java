/*
 * Handles HTTP REST communication with Timestreams
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A DataHandler for HTTP REST communication with Timestreams
 * @author pszjmb
 */
public class HttpReq implements DataHandler {
    String user = "";
    String pwrd = "";
    private final Map<String, ConsumeReqQueue> myQueues = new HashMap();
    List<Thread> consumerThreads = new ArrayList();//Thread(new ConsumeReqQueue(myQueue));
    SimpleDateFormat dateFormat;
    String time;
    boolean running = true;
    private final ReentrantLock reqLock = new ReentrantLock();
    TimestreamPoster client = null;
    
    /**
     * Can stop this system running
     * @param running is a boolean. False means stop.
     */
    @Override
    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * Adds a consumer queue to the queue list.
     * @param name  is the name of the data source (such as CO2)
     */
    public void addConsumerQueue(String name) {
        ConsumeReqQueue c = new ConsumeReqQueue(new LinkedBlockingQueue());
        myQueues.put(name, c);
        Thread t = new Thread(c);
        consumerThreads.add(t);
        t.start();
    }

    /**
     * Initialisation routine
     * @param params is a set Properties 
     */
    @Override
    public void init(Properties params) {
        if ("null".equals(params.getProperty("TimestreamsUrl", "null"))){
                System.err.println("Unknown timestreams URL. Make sure that the properties file has "
                        + "an entry like: <entry key=\"TimestreamsUrl\">"
                        + "http://timestreams.wp.horizon.ac.uk/xmlrpc.php</entry>");   
                return;
        }else{
            client = new TimestreamPoster(params.getProperty("TimestreamsUrl", "null"));
        }
        if (!("null".equals(params.getProperty("proxyUrl", "null")) || 
                "null".equals(params.getProperty("proxyPort", "null")))) {           
                System.setProperty("http.proxyHost", params.getProperty("proxyUrl"));
                System.setProperty("http.proxyPort", params.getProperty("proxyPort"));
            }
        if (!("null".equals(params.getProperty("username", "null")) || 
                "null".equals(params.getProperty("password", "null")))) {
            user = params.getProperty("username");
            pwrd = params.getProperty("password");
        } else{
            System.err.println("Unknown timestreams username or password. "
                + "Make sure that the properties file has "
                + "entries containing: key=\"username\", key=\"password\" "
                + "and key=\"apiCall\"."); 
            return;
        }
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone(params.getProperty("timeszone", "GMT")));
    }

    /**
     * Consumes data from the queues.
     * @param data is an array of Strings containing [data value,timestamp]
     */
    @Override
    public void execute(String[] data) {
        if(data.length!=2 || null == data[0] || "null".equals(data[0])){
            return;
        }
        time = dateFormat.format(new Date());
        //Object[] params = new Object[]{"admin","Time349","wp_1_ts_C02_66"};
        if (!myQueues.containsKey(data[0])) {
            addConsumerQueue(data[0]);
        }
        myQueues.get(data[0]).put(new ReqObj(new Object[]{user, pwrd, data[0], data[1], time}));
        //System.out.println(time + " " + data[0] + " " + data[1]);
    }

    /**
     * Consumer thread
     */
    private class ConsumeReqQueue implements Runnable {

        private final BlockingQueue<ReqObj> queue;
        private final ReentrantLock takeLock = new ReentrantLock();
        private int insertionCounter = 0;

        /**
         * Constructor
         * @param queue 
         */
        public ConsumeReqQueue(BlockingQueue<ReqObj> queue) {
            this.queue = queue;
        }

        /**
         * Adds request objects to the queue
         * @param ro 
         */
        public void put(ReqObj ro) {
            try {
                queue.put(ro);
            } catch (InterruptedException ex) {
                Logger.getLogger(HttpReq.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        /**
         * Consumes items from the queue
         */
        @Override
        public void run() {
            while (running) {
                final ReentrantLock takeLock1 = this.takeLock;
                Collection<ReqObj> tempCol = new ArrayList<ReqObj>();
                try {
                    takeLock1.lockInterruptibly();
                    tempCol.add(queue.take());
                    queue.drainTo(tempCol);
                    queue.clear();
                } catch (InterruptedException ex) {
                } finally {
                    takeLock1.unlock();
                }
                consume(tempCol);
            }
        }

        /**
         * Sends enqueued data to Timestreams
         * @param tempCol 
         */
        void consume(Collection<ReqObj> tempCol) {
            ReqObj req = null;
            Iterator<ReqObj> it = tempCol.iterator();
            boolean first = true;
            List params = new ArrayList();
            Object[] reqParams = null;
            while (it.hasNext()) {
                req = it.next();
                reqParams = req.getParams();
                if (first) {
                    first = false;
                    params.add(reqParams[0]);
                    params.add(reqParams[1]);
                    params.add(reqParams[2]);
                }
                params.add(reqParams[3]);
                params.add(reqParams[4]);
            }
            synchronized (client) {
                if (null != reqParams) {
                    System.out.println(//" " + ++insertionCounter + " " + 
                            client.post(params));
                }
            }
        }
    }

    /**
     * A request object
     */
    private class ReqObj {

        private Object[] params;

        public ReqObj(Object[] params) {
            this.params = params;
        }

        public Object[] getParams() {
            return params;
        }

        public void setParams(Object[] params) {
            this.params = params;
        }
    }
}
