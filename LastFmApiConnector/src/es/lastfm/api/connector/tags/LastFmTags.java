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
    /*Devuelve una lista de las tags con las que va a funcionar la aplicaci�n*/
    public String[] getWorkingTags();
    
    /*Devuelve una lista de tags que estar�n disponibles en la app pero que no estar�n seleccionadas por defecto*/
    public String[] getNotDefaultTags();
    
    public String toString();
    
    public String getLongDescription();
}
