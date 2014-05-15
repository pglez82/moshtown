/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.lastfm.api.connector.dto;

import de.umass.lastfm.Event;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

/**
 *
 * @author pablo
 */
public class ArtistEventDTO 
{
    private Event event;

    public ArtistEventDTO(Event event) 
    {
        this.event = event;
    }
    
    public Date getStartDate()
    {
        return event.getStartDate();
    }
    
    public String getEventPlace()
    {
        return event.getVenue()!=null ?  event.getVenue().getName()+" ("+event.getVenue().getCity()+')' : "";
    }
    
    public Collection<String> getEventArtists()
    {
        return event.getArtists();
    }
    
    public int getEventId()
    {
        return event.getId();
    }
    
    public Date getEndDate()
    {
        return event.getEndDate();
    }
    
    public String getEventTitle()
    {
        return event.getTitle();
    }
    
    public double getLatEventPlace()
    {
        return event.getVenue()!=null ? event.getVenue().getLatitude() : 0;
    }
    
    public double getLonEventPlace()
    {
        return event.getVenue()!=null ? event.getVenue().getLongitude() : 0;
    }
    
}
