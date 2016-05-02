/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.isis2503.logic;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 *
 * @author Felipe
 */
public class ZooKeeperMannager 
{
    private static CuratorFramework client;
    public static ZooKeeperMannager instance;
    
    
    public ZooKeeperMannager()
    {
        createConnection();
    }
    
    public void createConnection ()
    {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.newClient("localHost:2181", retryPolicy);
        client.start();
    }
    
    public static ZooKeeperMannager getInstance()
    {
        if(client == null || !client.getState().equals(CuratorFrameworkState.STARTED)  )
            instance = new ZooKeeperMannager();
        return instance;
    }
    
    public void createNode(String fullPath, String data) throws Exception
    {
        if(client == null || !client.getState().equals(CuratorFrameworkState.STARTED)  )
            createConnection();
        client.create().forPath(fullPath, data.getBytes());
    }
    
    public String getNodeData(String fullPath) throws Exception
    {
        
        if(client == null)
            createConnection();
        if (checkExistNode(fullPath))
        {
            
            byte[] n = client.getData().forPath(fullPath);
        
            return new String(n);
        }
        return null;
    }
    
    public void deleteNode(String fullPath)throws Exception
    {
        if(client == null || !client.getState().equals(CuratorFrameworkState.STARTED)  )
            createConnection();
        client.delete().deletingChildrenIfNeeded().forPath(fullPath);
    }
    
    public boolean checkExistNode(String fullPath)throws Exception
    {
        if(client == null || !client.getState().equals(CuratorFrameworkState.STARTED)  )
            createConnection();
        return client.checkExists()!= null;
    }
    
    public void closeConnection() throws Exception
    {
        if(client == null || !client.getState().equals(CuratorFrameworkState.STARTED)  )
            client.close();
    }
    
    public boolean setData(String fullPath, String data) throws Exception
    {
        if(client == null || !client.getState().equals(CuratorFrameworkState.STARTED)  )
            createConnection();
        if(checkExistNode(fullPath))
        {
            client.setData().forPath(fullPath,data.getBytes());
            return true;
        }
        return false;
    } 
    
}
