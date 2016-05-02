/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.isis2503.services;

import co.edu.uniandes.isis2503.logic.InstanceDetails;
import co.edu.uniandes.isis2503.logic.ZooKeeperServerMannager;
import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceType;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.server.contexts.GenericDiscoveryContext;
import org.apache.curator.x.discovery.server.rest.DiscoveryResource;
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Felipe
 */
@Path("/server")
public class DirectoryZKServices 
{
    
    private DiscoveryResource<InstanceDetails> zooKeeperServerMannager;
    private CuratorFramework client;
    public static final String ZK_HOST = "localhost";
    public static final String PORT = "2181";
    
    
     
    @PostConstruct
    public void init() 
    {
        try 
        {    
            String  zkURL = ZK_HOST + ":" + PORT;
            System.out.println("Conectando ZK 1 -- " + zkURL);
            client= CuratorFrameworkFactory.newClient(zkURL, new RetryNTimes(5, 1000));
            System.out.println("Conectando ZK 2");
            client.start();
            System.out.println("Conectando ZK 3");
            JsonInstanceSerializer<InstanceDetails> serializer = new JsonInstanceSerializer<InstanceDetails>(InstanceDetails.class);
            System.out.println("Conectando ZK 4");
            ServiceDiscovery<InstanceDetails> sDiscovery = ServiceDiscoveryBuilder.builder(InstanceDetails.class)
                    .client(client).basePath("arquiSW/").serializer(serializer).build();
            System.out.println("Conectando ZK 5");
            sDiscovery.start();
            System.out.println("Conectando ZK 6");
            GenericDiscoveryContext<InstanceDetails> dContext = new GenericDiscoveryContext(sDiscovery, new RoundRobinStrategy<InstanceDetails>(), 0, Void.TYPE);
            System.out.println("Conectando ZK 7");
            zooKeeperServerMannager = new ZooKeeperServerMannager<InstanceDetails>(dContext);
            System.out.println("Conectando ZK 8");
            
            
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
            long registerTime = in.getLong("registrationTimeUTC");

            ServiceInstance<InstanceDetails> service = new ServiceInstance<InstanceDetails>(name, id, address, port, Integer.SIZE, null, registerTime, ServiceType.valueOf(serviceType), null);

            return zooKeeperServerMannager.putService(service,name,id);            
            
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
    
    //TODO No funciona
    @DELETE
    @Path("/service/{name}/{id}")
    public Response removeService(@PathParam("name") String name, @PathParam("id") String id)
    {
        JSONObject rta = new JSONObject();
        try
        {
            return zooKeeperServerMannager.removeService(name, id);            
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
            return zooKeeperServerMannager.get(name, id);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity("ERROR HACIENDOLO").build();
        }
    }
    //TODO No funciona
    @GET
    @Path("/service")
    public Response getAllServices()
    {
        System.out.println("Pidiendo servicios 1");
        JSONObject rta = new JSONObject();
        try
        {
            System.out.println("Pidiendo servicios 2");
            return zooKeeperServerMannager.getAllNames();
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
            return zooKeeperServerMannager.getAll(name);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity("ERROR HACIENDOLO").build();
        }
    }
    //TODO No es Random
    @GET
    @Path("/anyservice/{name}")
    public Response getAny(@PathParam("name") String name)
    {
        JSONObject rta = new JSONObject();
        try
        {
            return zooKeeperServerMannager.getAny(name);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity("ERROR HACIENDOLO").build();
        }
    }
    
}
