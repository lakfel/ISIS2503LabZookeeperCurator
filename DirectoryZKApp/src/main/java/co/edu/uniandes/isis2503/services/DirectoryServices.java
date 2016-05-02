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
public class DirectoryServices {

    private ZooKeeperDiscoveryMannager zooKeeperDiscoveryMannager;
    public static final String ZK_HOST = "localhost";
    public static final String PORT = "2181";

    @PostConstruct
    public void init() {
        try {
            String zkURL = ZK_HOST + ":" + PORT;
            zooKeeperDiscoveryMannager = new ZooKeeperDiscoveryMannager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DELETE
    @Path("/service/{name}/{id}")
    public Response removeService(@PathParam("name") String name, @PathParam("id") String id) {
        JSONObject rta = new JSONObject();
        try {
            return zooKeeperDiscoveryMannager.removeService(name, id);
        } catch (Exception e) {
            try {
                rta.put("Estado", "ERROR");
            } catch (Exception e2) {
                e2.printStackTrace();

            }
            e.printStackTrace();
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta).build();
    }

    @GET
    @Path("/service/{name}/{id}")
    public Response get(@PathParam("name") String name, @PathParam("id") String id) {
        try {
            return zooKeeperDiscoveryMannager.get(name, id);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity("ERROR HACIENDOLO").build();
        }
    }

    @GET
    @Path("/service")
    public Response getAllServices() {
        try {
            return zooKeeperDiscoveryMannager.getAllNames();
        } catch (Exception e) {

            e.printStackTrace();
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity("ERROR HACIENDOLO").build();
        }
    }

    @GET
    @Path("/service/{name}")
    public Response getAll(@PathParam("name") String name) {
        try {
            return zooKeeperDiscoveryMannager.getAll(name);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity("ERROR HACIENDOLO").build();
        }
    }

    @GET
    @Path("/anyservice/{name}")
    public Response getAny(@PathParam("name") String name) {
        try {
            return zooKeeperDiscoveryMannager.getAny(name);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity("ERROR HACIENDOLO").build();
        }
    }

}
