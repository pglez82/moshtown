package es.concertsapp.android.utils;

import org.apache.http.MethodNotSupportedException;

import java.util.Set;

import es.concertsapp.android.conf.ConfValues;
import es.concertsapp.android.gui.settings.SelectedTagsStore;
import es.lastfm.api.connector.LastFmApiConnector;
import es.lastfm.api.connector.tags.LastFmTags;
import es.lastfm.api.connector.tags.PunkTags;

/**
 * Factoria de la clase de acceso al webservice. Implementa el patrón singleton
 */
public class LastFmApiConnectorFactory 
{
	//private static LastFmTags lastFmTags = new PunkTags();
	private static LastFmApiConnector lastFmApiConnector = null;

    private static LastFmTags getTags()
    {
        final SelectedTagsStore selectedTagsStore = SelectedTagsStore.getInstance();
        return new LastFmTags() {
            @Override
            public String[] getWorkingTags() {
                Set<String> selectedTags = selectedTagsStore.getSelectedTags();
                return selectedTags.toArray(new String[selectedTags.size()]);
            }

            @Override
            public String getLongDescription() {
                return "Tags personalizadas";
            }

            @Override
            public String[] getNotDefaultTags() {
                throw new IllegalArgumentException("No se debería de llamar a este método aquí nunca");
            }
        };
    }

	public static LastFmApiConnector getInstance()
	{
		if (lastFmApiConnector == null)
			lastFmApiConnector = new LastFmApiConnector(getTags(), new ConfValues(), MyApplication.getLocale());
		
		return lastFmApiConnector;
	}
	
	public static LastFmApiConnector getNewInstance()
	{
		lastFmApiConnector = new LastFmApiConnector(getTags(), new ConfValues(), MyApplication.getLocale());
		return lastFmApiConnector;
	}

}
