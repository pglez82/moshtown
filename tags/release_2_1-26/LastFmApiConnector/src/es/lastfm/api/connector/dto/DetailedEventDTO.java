/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.lastfm.api.connector.dto;

import de.umass.lastfm.Event;
import de.umass.lastfm.ImageSize;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author pablo
 */
public class DetailedEventDTO implements LastFmImageSourceI
{
    private Event event;

    public DetailedEventDTO(Event event) 
    {
        this.event = event;
    }
    
    public String getTitle()
    {
        return event.getTitle();
    }
    
    public String getDescription()
    {
        return event.getDescription();
    }
    
    public String getHeadLiner()
    {
        return event.getHeadliner();
    }
    
    public Collection<String> getArtists()
    {
        return event.getArtists();
    }
    
    public String getVenueName()
    {
        return event.getVenue()!=null ? event.getVenue().getName() : "";
    }
    
    public Date getStartDate()
    {
        return event.getStartDate();
    }
    
    public Date getEndDate()
    {
        return event.getEndDate();
    }
    
    public String getEventPlace()
    {
        return event.getVenue()!=null ? event.getVenue().getName()+" ("+event.getVenue().getCity()+')' : "";
    }
    
    public double getLatEventPlace()
    {
        return event.getVenue()!=null ? event.getVenue().getLatitude() : 0;
    }
    
    public double getLonEventPlace()
    {
        return event.getVenue()!=null ? event.getVenue().getLongitude() : 0;
    }
    
    public Collection<Event.TicketSupplier> getTicketsInfo()
    {
        return event.getTicketSuppliers();
    }
    
    public String getImageURL(ImageSize imageSize)
    {
        return event.getImageURL(imageSize);
    } 
    
    public String getAddress()
    {
        return event.getVenue()!=null ? event.getVenue().getStreet() : "";
    }

    public int getId()
    {
        return event.getId();
    }
}
