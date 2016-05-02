/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.isis2503.services;

import co.edu.uniandes.isis2503.main.Main;
import co.edu.uniandes.isis2503.persistence.PersistenceManager;
import co.edu.uniandes.isis2503.models.Competition;
import co.edu.uniandes.isis2503.models.CompetitorDTO;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Felipe
 */
@Path("/competitions")
@Produces(MediaType.APPLICATION_JSON)
public class CompetitionServices 
{
    @PersistenceContext(unitName = "CompetitionsPU")
    EntityManager entityManager;
    
    @PostConstruct
    public void init() {
        try {
            entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() 
    {
        Query q = entityManager.createQuery("select u from Competencia u order by u.name ASC"); 
        List<Competition> competitors = q.getResultList();
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(competitors).build();
       
    }
    
    @GET
    @Path("/winners/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllByID(@PathParam("name") String name) 
    {
         Query q = entityManager.createQuery("select u from Competencia u where u.name = '" + name +"' order by u.name ASC"); 
         List<Competition> competencias = q.getResultList();
         List<CompetitorDTO> competidores = new ArrayList<CompetitorDTO>();
         try 
         {    
            Client client = Client.create();
            WebResource target = client.resource(Main.SERVIDOR_ZK+"/service/"+name);
            JSONObject pag = target.get(JSONObject.class);
            JSONObject uriSpec = pag.getJSONObject("uriSpec");
            JSONArray parts = uriSpec.getJSONArray("parts");
            String url = "";
             for (int i = 0; i <parts.length(); i++) 
             {
                 if(parts.getJSONObject(i).getBoolean("variable"))
                     url +="{" + parts.getJSONObject(i).getString("value") + "}";
                 else
                     url += parts.getJSONObject(i).getString("value");
                 
             }
            
            String url2 = "";
            for (int i = 0; i < competencias.size(); i++) 
             {
                url2 = url;
                url2.replace("{id}", "" +competencias.get(i).getWinnerId());
                target = client.resource(url2);
                competidores.addAll(target.get(List.class));        
             }
            client.destroy();
 
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
         return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(competidores).build();
    }
    
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createContest(Competition competencia) {
        JSONObject rta = new JSONObject();
        Competition competenciaTmp = new Competition();
        competenciaTmp.setName(competencia.getName());
        competenciaTmp.setCity(competencia.getCity());
        competenciaTmp.setCountry(competencia.getCountry());
        competenciaTmp.setPrize(competencia.getPrize());
        competenciaTmp.setYear(competencia.getYear());
        competenciaTmp.setWinnerId(competencia.getWinnerId());
     
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(competenciaTmp);
            entityManager.getTransaction().commit();
            entityManager.refresh(competenciaTmp);
            rta.put("competencia_id", competenciaTmp.getId());
        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            competenciaTmp = null;
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta).build();
    } 

    
}
