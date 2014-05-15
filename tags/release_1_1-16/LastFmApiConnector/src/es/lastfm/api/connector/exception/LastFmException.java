/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.lastfm.api.connector.exception;

/**
 *
 * @author pablo
 */
public class LastFmException extends Throwable
{
    public LastFmException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
