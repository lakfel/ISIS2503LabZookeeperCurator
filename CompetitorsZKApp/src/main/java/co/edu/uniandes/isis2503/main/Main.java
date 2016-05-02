package co.edu.uniandes.isis2503.main;

import co.edu.uniandes.isis2503.persistence.PersistenceManager;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 *
 * This class launches the web application in an embedded Jetty container. This
 * is the entry point to your application. The Java command that is used for
 * launching should fire this main method.
 *
 */
public class Main 
{
    
    
    

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        String webappDirLocation = "src/main/webapp/";
        String webPort = System.getenv("PORT");
        if (webPort == null || webPort.isEmpty()) {
            webPort = ServiceRegister.HOST_PORT + "";
        }
        Server server = new Server(Integer.valueOf(webPort));
        WebAppContext root = new WebAppContext();
        root.setContextPath("/");
        root.setDescriptor(webappDirLocation + "/WEB-INF/web.xml");
        root.setResourceBase(webappDirLocation);
        PersistenceManager.getInstance().getEntityManagerFactory();
        root.setParentLoaderPriority(true);
        server.setHandler(root);
        
        ServiceRegister.registerService("Competitors", "/competitors");
        ServiceRegister.registerService("CompetitorsById", "/competitors/{id}");
        
        server.start();
        server.join();
    }
    
    
    

}
