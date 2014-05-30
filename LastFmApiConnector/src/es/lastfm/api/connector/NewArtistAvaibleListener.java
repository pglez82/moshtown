/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.lastfm.api.connector;

import es.lastfm.api.connector.dto.ArtistDTO;

/**
 *
 * @author pablo
 */
public interface NewArtistAvaibleListener 
{
    public void newArtistAvailable(ArtistDTO eventDTO);
}
