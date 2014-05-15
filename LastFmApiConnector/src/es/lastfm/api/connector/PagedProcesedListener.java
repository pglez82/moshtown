/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.lastfm.api.connector;

/**
 *
 * @author pablo
 */
public interface PagedProcesedListener
{
    /**
     * 
     * @param page Página que acabamos de procesar (nuestra)
     * @param totalPages número total de págians que ha devuelto last.fm
     */
    public void pagedProcessed(int page, int totalPages);
}
