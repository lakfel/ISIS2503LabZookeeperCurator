/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.isis2503.logic;

import org.codehaus.jackson.map.annotate.JsonRootName;



/**
 *
 * @author Felipe
 */
/**
 * In a real application, the Service payload will most likely
 * be more detailed than this. But, this gives a good example.
 */
@JsonRootName("details")
public class InstanceDetails
{
    private String        description;

    public InstanceDetails()
    {
        this("");
    }

    public InstanceDetails(String description)
    {
        this.description = description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }
}