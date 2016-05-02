/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.isis2503.services;

import co.edu.uniandes.isis2503.logic.InstanceDetails;
import co.edu.uniandes.isis2503.logic.ZooKeeperDiscoveryMannager;
import javax.annotation.PostConstruct;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceType;
import org.apache.curator.x.discovery.UriSpec;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Felipe
 */

@Path("/directory")
public class DirectoryServices 
{
    
    private ZooKeeperDiscoveryMannager zooKeeperDiscoveryMannager;
    public static final String ZK_HOST = "localhost";
    public static final String PORT = "2181";
    
    
     
    @PostConstruct
    public void init() 
    {
        try 
        {    
            String  zkURL = ZK_HOST + ":" + PORT;
            zooKeeperDiscoveryMannager = new ZooKeeperDiscoveryMannager();
            
           
            
            
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    

    
    @POST
    @Path("/service")
    public Response putService(JSONObject in)
    {
        JSONObject rta = new JSONObject();
        try
        {
            String name= in.getString("name");
            String id= in.getString("id");
            String address= in.getString("address");
            int port = in.getInt("port");
            String serviceType = in.getString("serviceType");
            String uriSpe = in.getString("uriSpec");

            UriSpec uriSpec = new UriSpec("http://"+address+":"+port+uriSpe);
            
            ServiceInstance<InstanceDetails> service = new ServiceInstance<InstanceDetails>(name, id, address, port, Integer.SIZE, null, 0, ServiceType.valueOf(serviceType), uriSpec);

            return zooKeeperDiscoveryMannager.putService(service,name,id);            
            
        }
        catch(Exception e)
        {
            try
            {
                rta.put("Estado","ERROR");
            }
            catch(Exception e2)
            {
                e2.printStackTrace();
            
            }
            e.printStackTrace();
        }
         return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta).build();
    }
    
    // TODO NO FUNCIONA
    @DELETE
    @Path("/service/{name}/{id}")
    public Response removeService(@PathParam("name") String name, @PathParam("id") String id)
    {
        JSONObject rta = new JSONObject();
        try
        {
            return zooKeeperDiscoveryMannager.removeService(name, id);            
        }
        catch(Exception e)
        {
            try
            {
                rta.put("Estado","ERROR");
            }
            catch(Exception e2)
            {
                e2.printStackTrace();
            
            }
            e.printStackTrace();
        }
         return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta).build();
    }
    
    
    
    @GET
    @Path("/service/{name}/{id}")
    public Response get(@PathParam("name") String name, @PathParam("id") String id)
    {
        JSONObject rta = new JSONObject();
        try
        {
            return zooKeeperDiscoveryMannager.get(name, id);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity("ERROR HACIENDOLO").build();
        }
    }
    
    @GET
    @Path("/service")
    public Response getAllServices()
    {
        System.out.println("Pidiendo servicios 1");
        JSONObject rta = new JSONObject();
        try
        {
            System.out.println("Pidiendo servicios 2");
            return zooKeeperDiscoveryMannager.getAllNames();
        }
        catch(Exception e)
        {
            
            e.printStackTrace();
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity("ERROR HACIENDOLO").build();
        }
    }
    @GET
    @Path("/service/{name}")
    public Response getAll(@PathParam("name") String name)
    {
        JSONObject rta = new JSONObject();
        try
        {
            return zooKeeperDiscoveryMannager.getAll(name);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity("ERROR HACIENDOLO").build();
        }
    }
    
    @GET
    @Path("/anyservice/{name}")
    public Response getAny(@PathParam("name") String name)
    {
        JSONObject rta = new JSONObject();
        try
        {
            return zooKeeperDiscoveryMannager.getAny(name);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity("ERROR HACIENDOLO").build();
        }
    }
    
}

