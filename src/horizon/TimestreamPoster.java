/*
 * Posts data to Timestreams using the REST API
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

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Posts data to Timestreams using the REST API
 * @author pszjmb
 */
public class TimestreamPoster {
    String urlBase;
    public TimestreamPoster(String urlBaseIn){
        urlBase = urlBaseIn;
    }

    public String getUrlBase() {
        return urlBase;
    }

    public void setUrlBase(String urlBase) {
        this.urlBase = urlBase;
    }
    
    /**
     * HTTP Post data to Timestreams
     * @param params are the data to post
     * @return the HTTP response and params as a String
     */
    public String post(List params) {
        if(params.size() < 5){
            return null;
        }
        String user = (String)params.get(0);
        String password = (String)params.get(1);
        String measurementContainerName = (String)params.get(2);
        String response = null;
        try {
            StringBuilder sb = new StringBuilder("measurements={\"measurements\":[");
            for(int i = 3; i < params.size(); i++){
                if(i%2 ==0){
                    sb.append("\"t\":\"").append(params.get(i)).append("\"},");  
                }else{
                    sb.append("{\"v\":\"").append(params.get(i)).append("\",");                    
                }
             }
            sb.delete(sb.length()-1, sb.length());
            sb.append("]}");
            String urlParameters = sb.toString();
            String request = urlBase + measurementContainerName;
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
            connection.setUseCaches(false);

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            response = "Result: " + connection.getResponseCode() + " params: " + params;
            connection.disconnect();
        } catch (IOException ex) {
            return TimestreamPoster.class.getName() + " IOException";
        }
        return response;        
    }
}
