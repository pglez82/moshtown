/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.lastfm.api.connector;

import es.lastfm.api.connector.dto.EventDTO;

/**
 *
 * @author pablo
 */
public interface NewEventAvaibleListener 
{
    public void newElementAvailable(EventDTO eventDTO);
}
