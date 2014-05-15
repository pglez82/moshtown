/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.lastfm.api.connector.configuration;

/**
 *
 * @author pablo
 */
public interface LastFmApiConfiguration
{
    //Tamaño máximo de la lista de tags del artista a analizar.
    public int getMaxTopTagsArtist();
    //Tamaño máximo de artistas a analizar en los eventos en el caso de que el evento
    //no tenga tags
    public int getMaxArtistsEventTags();
}
