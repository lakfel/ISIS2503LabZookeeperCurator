/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.isis2503.logic;

import java.io.Closeable;
import java.io.IOException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceType;
import org.apache.curator.x.discovery.UriSpec;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

/**
 *
 * @author Felipe
 */
public class ZooKeeperServiceRegister implements Closeable 
{
   
    private  ServiceDiscovery<InstanceDetails> serviceDiscovery;
    private  ServiceInstance<InstanceDetails> thisInstance;

    public ZooKeeperServiceRegister(CuratorFramework client,String id, String address, int nPort, String path, String serviceName, String description, String serviceType, String relPath) throws Exception
    {
        
        UriSpec     uriSpec = new UriSpec("http://"+address+":"+nPort+relPath);

        thisInstance = ServiceInstance.<InstanceDetails>builder()
            .address(address)
            .serviceType(ServiceType.valueOf(serviceType))
            .id(id)
            .name(serviceName)
            .payload(new InstanceDetails(description))
            .port(nPort) // in a real application, you'd use a common port
            .uriSpec(uriSpec)
            .build();

        // if you mark your payload class with @JsonRootName the provided JsonInstanceSerializer will work
        JsonInstanceSerializer<InstanceDetails> serializer = new JsonInstanceSerializer<InstanceDetails>(InstanceDetails.class);

        serviceDiscovery = ServiceDiscoveryBuilder.builder(InstanceDetails.class)
            .client(client)
            .basePath(path)
            .serializer(serializer)
            .thisInstance(thisInstance)
            .build();
    }
    
    public ZooKeeperServiceRegister(CuratorFramework client,ServiceInstance si, String path, String serviceName, String id) throws Exception
    {
        
        

        thisInstance = si;

        // if you mark your payload class with @JsonRootName the provided JsonInstanceSerializer will work
        JsonInstanceSerializer<InstanceDetails> serializer = new JsonInstanceSerializer<InstanceDetails>(InstanceDetails.class);
        
        System.out.println("Estado    " + client.getState().name());
        serviceDiscovery = ServiceDiscoveryBuilder.builder(InstanceDetails.class)
            .client(client)
            .basePath(path)
            .serializer(serializer)
            .thisInstance(thisInstance)
            .build();
    }
    

    public ServiceInstance<InstanceDetails> getThisInstance()
    {
        return thisInstance;
    }

    public void start() throws Exception
    {
        
        serviceDiscovery.start();
    }

    @Override
    public void close() throws IOException
    {
        CloseableUtils.closeQuietly(serviceDiscovery);
    }
    
}
