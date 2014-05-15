/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.lastfm.api.connector.tags;

import java.util.List;

/**
 *
 * @author pablo
 */
public interface LastFmTags
{
    /*Devuelve una lista de las tags con las que va a funcionar la aplicación*/
    public String[] getWorkingTags();
    
    public String toString();
    
    public String getLongDescription();
}
