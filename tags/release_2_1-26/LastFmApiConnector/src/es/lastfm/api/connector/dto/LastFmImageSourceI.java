/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.lastfm.api.connector.dto;

import de.umass.lastfm.ImageSize;

/**
 *
 * @author pablo
 */
public interface LastFmImageSourceI
{
    public String getImageURL(ImageSize imageSize);
}
