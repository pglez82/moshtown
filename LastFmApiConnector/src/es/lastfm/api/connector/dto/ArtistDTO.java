/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.lastfm.api.connector.dto;

import de.umass.lastfm.Artist;
import de.umass.lastfm.ImageSize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author pablo
 */
public class ArtistDTO implements Serializable, LastFmImageSourceI
{
    private Artist artist;
    //Indica si hay conciertos cerca o no.
    private transient boolean nearEvents=false;
    private transient boolean loadingNearEvents=false;

    public ArtistDTO(Artist artist) 
    {
        this.artist = artist;
    }
    
    public String getArtistName()
    {
        return artist.getName();
    }
    
    public String getSummary()
    {
        return artist.getWikiSummary();
    }
    
    public String getImageURL(ImageSize imageSize)
    {
        return artist.getImageURL(imageSize);
    } 
    
    public List<ArtistDTO> getSimilarArtists()
    {
        List<ArtistDTO> listSimilar = new ArrayList<ArtistDTO>();
        for (Artist similarArtist : artist.getSimilar())
        {
            listSimilar.add(new ArtistDTO(similarArtist));
        }
        return listSimilar;
    }
    
    public Collection<String> getArtistTags()
    {
        return artist.getTags();
    }

    public boolean isNearEvents()
    {
        return nearEvents;
    }

    public void setNearEvents(boolean nearEvents)
    {
        this.nearEvents = nearEvents;
    }

    public boolean isLoadingNearEvents()
    {
        return loadingNearEvents;
    }

    public void setLoadingNearEvents(boolean loadingNearEvents)
    {
        this.loadingNearEvents = loadingNearEvents;
    }
    

    @Override
    public boolean equals(Object obj)
    {
        if (obj==null || !(obj instanceof ArtistDTO))
            return false;
        else
        {
            ArtistDTO otro = (ArtistDTO)obj;
            if (artist.getName().equals(otro.artist.getName()))
                return true;
            else
                return false;
        }
    }
    
    
}
