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
     * @param page P�gina que acabamos de procesar (nuestra)
     * @param totalPages n�mero total de p�gians que ha devuelto last.fm
     */
    public void pagedProcessed(int page, int totalPages);
}
