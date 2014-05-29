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
     * @param nuevos eventos que hemos encontrado en esta iteracción (esta pag)
     * @param totalEventsFound número total de eventos que llevamos encontrados
     * @param distance distancia a la que estamos buscando
     */
    public void pagedProcessed(int page, int totalPages, int nuevos, int totalEventsFound, int distance);
}
