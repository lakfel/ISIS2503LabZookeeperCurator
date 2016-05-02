/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.isis2503.main;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Felipe
 */
public class ServiceRegister
{
    public static final String HOST_ADDRESS = "10.20.30.50";//IP del servicio
    public static final int HOST_PORT = 8082;//Puerto del servicio
    public static final String ID = "200921691";// Identificador del servidor, código del estudiante
    public static final String SERVIDOR_ZK = "http://localhost:8080/directory/"; //Acá va el host del servidor ZK
    
    
    public static void registerService(String serviceName, String relativePath)
    {
        try 
        {
            
                
            JSONObject datos = new JSONObject();
            datos.put("name",serviceName);
            datos.put("id",ID);
            datos.put("address",HOST_ADDRESS);
            datos.put("port",HOST_PORT);
            datos.put("registrationTimeUTC",System.currentTimeMillis());
            datos.put("serviceType","STATIC");
            datos.put("uriSpec",relativePath);
           
 
 
 
 
            Client client = Client.create();
            WebResource target = client.resource(SERVIDOR_ZK + "service");
            target.post(JSONObject.class,datos);
           
 
 
 
            client.destroy();
 
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    
    
}
