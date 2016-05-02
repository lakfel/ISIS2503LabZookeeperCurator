package co.edu.uniandes.isis2503.logic;


import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.strategies.RandomStrategy;
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;



    

public class ZooKeeperDiscoveryMannager
{
    
    // TODO --- Manejar los servers mejor
    private static final String PATH = "/ISIS2503";
    private CuratorFramework client ;
    private ServiceDiscovery<InstanceDetails> serviceDiscovery;
    private Map<String, ServiceProvider<InstanceDetails>>providers = Maps.newHashMap();
    private List<ZooKeeperServiceRegister>     servers;
    
    public static final String ZK_HOST = "localhost";
    public static final String PORT = "2181";
    
    public ZooKeeperDiscoveryMannager()
    {
        client = null;
        serviceDiscovery = null;
        providers = Maps.newHashMap();
        servers = Lists.newArrayList();
        String  zkURL = ZK_HOST + ":" + PORT;
        try
        {
            client = CuratorFrameworkFactory.newClient(zkURL, new ExponentialBackoffRetry(1000, 3));
            System.out.println("INICIANDO CONEXION --- ");
            client.start();
            System.out.println("CONEXION ESTADO --- " + client.getState().name());

            JsonInstanceSerializer<InstanceDetails> serializer = new JsonInstanceSerializer<InstanceDetails>(InstanceDetails.class);
            serviceDiscovery = ServiceDiscoveryBuilder.builder(InstanceDetails.class).client(client).basePath(PATH).serializer(serializer).build();
            serviceDiscovery.start();

            System.out.println("CONEXION ESTADO --- " + client.getState().name());

            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            for ( ServiceProvider<InstanceDetails> cache : providers.values() )
            {
                CloseableUtils.closeQuietly(cache);
            }

            //CloseableUtils.closeQuietly(serviceDiscovery);
            //CloseableUtils.closeQuietly(client);
            
        }
    }
    
    
    public Response putService(ServiceInstance<InstanceDetails> si, String name, String id) throws Exception
    {
        ZooKeeperServiceRegister newServiceInstance = new ZooKeeperServiceRegister(client, si, PATH, name, id);
        newServiceInstance.start();
        servers.add(newServiceInstance);
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(newServiceInstance).build();
    }
    
    public Response removeService(String name, String id)
    {
        final String  nameS = name;
        ZooKeeperServiceRegister  server = Iterables.find
        (
            servers,
            new Predicate<ZooKeeperServiceRegister>()
            {
                @Override
                public boolean apply(ZooKeeperServiceRegister server)
                {
                    return server.getThisInstance().getName().endsWith(nameS);
                }
            },
            null
        );
        
        if ( server == null )
        {
            System.err.println("No servers found named: " + nameS);
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity("NOT FOUND").build();
        }

        servers.remove(server);
        CloseableUtils.closeQuietly(server);
        System.out.println("Removed a random instance of: " + nameS);
        
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity("DELETED").build();
    }
    
    public Response get(String serviceName, String id) throws Exception
    {
        ServiceProvider<InstanceDetails> provider = providers.get(serviceName);
        if(provider == null)
        {
            provider = serviceDiscovery.serviceProviderBuilder().serviceName(serviceName).providerStrategy(new RandomStrategy<InstanceDetails>()).build();
            providers.put(serviceName, provider);
            provider.start();
        }
        Collection  <ServiceInstance<InstanceDetails>> instances= provider.getAllInstances();
        ServiceInstance<InstanceDetails> instance = null;
        if (instances != null)
        {
            Iterator<ServiceInstance<InstanceDetails>> a =  instances.iterator();
            while(a.hasNext() && instance == null)
            {
                ServiceInstance nn = a.next();
                instance=(nn.getId().equals(id))?nn:null;
            }
        }
        if(instance == null)
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity("NOT FOUND").build();
        else
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(instance).build();
       
    }
    
    public Response getAll(String serviceName) throws Exception
    {
        ServiceProvider<InstanceDetails> provider = providers.get(serviceName);
        Collection<ServiceProvider<InstanceDetails>> all = new ArrayList<ServiceProvider<InstanceDetails>>();
        if(provider == null)
        {
            provider = serviceDiscovery.serviceProviderBuilder().serviceName(serviceName).providerStrategy(new RandomStrategy<InstanceDetails>()).build();
            providers.put(serviceName, provider);
            provider.start();
        }
        Collection  <ServiceInstance<InstanceDetails>> instances= provider.getAllInstances();
        
        if(instances == null)
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity("NOT FOUND").build();
        else
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(instances).build();
       
    }
    
    public Response getAllNames() throws Exception
    {
        Collection<String> names =  serviceDiscovery.queryForNames();
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(names).build();
       
    }
    
    public Response getAny(String serviceName) throws Exception
    {
         ServiceProvider<InstanceDetails> provider = null ;//providers.get(serviceName);
       // if(provider == null)
        //{
            provider = serviceDiscovery.serviceProviderBuilder().serviceName(serviceName).providerStrategy(new RandomStrategy<InstanceDetails>()).build();
         //   providers.put(serviceName, provider);
            provider.start();
        //}
        ServiceInstance<InstanceDetails> instance= provider.getInstance();
        
      
        if(instance == null) 
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity("NOT FOUND").build();
        else
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(instance).build();
       
    }

    
}