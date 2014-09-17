package es.concertsapp.android.gui.settings;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.concertsapp.android.conf.ConfValues;
import es.concertsapp.android.utils.MyApplication;
import es.lastfm.api.connector.tags.LastFmTags;
import es.lastfm.api.connector.tags.PunkTags;
import es.lastfm.api.connector.tags.PunkTagsExtended;

/**
 * Created by pablo on 29/05/14.
 * Esta clase almacena las tags seleccionadas por el usuario en la ventana de settings.
 */
public class SelectedTagsStore
{
    private static SelectedTagsStore instance;
    private Context context;
    //Conjunto de tags seleccionadas
    private Set<String> selectedTags;

    //Esto es provisional.. simplemente tengo estas instancias aquí para poder sacar un listado de tags.
    private LastFmTags punkTags = new PunkTags();

    private SelectedTagsStore()
    {
        context = MyApplication.getAppContext();
    }

    public static SelectedTagsStore getInstance()
    {
        if (instance==null)
            instance = new SelectedTagsStore();

        return instance;
    }

    /**
     * Carga la tags seleccionadas desde un fichero de disco
     * @return conjunto de tags seleccionadas
     */
    private Set<String> loadSelectedTags()
    {
        Set<String> selectedTags = null;
        try
        {
            FileInputStream fis = context.openFileInput(ConfValues.FILENAME_TAGS);
            ObjectInputStream is = new ObjectInputStream(fis);
            selectedTags = (Set<String>) is.readObject();
            is.close();
            fis.close();
        }
        catch (Throwable e)
        {
            e.printStackTrace();

        }
        finally
        {
            if (selectedTags==null)
            {
                selectedTags = new HashSet<String>();
            }
        }
        return selectedTags;
    }

    /**
     * Salva la lista de tags a disco
     * @param selectedTags lista de tags
     */
    public void saveSelectedTags(Set<String> selectedTags)
    {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(ConfValues.FILENAME_TAGS, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(selectedTags);
            oos.close();
            fos.close();
            this.selectedTags=new HashSet<String>(selectedTags);
        } catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public Set<String> getSelectedTags() {
        if (selectedTags==null)
            selectedTags = loadSelectedTags();

        //Si está vacío, devolvemos las punktags como mínimo
        if (selectedTags.isEmpty())
            selectedTags.addAll(Arrays.asList(punkTags.getWorkingTags()));

        return new HashSet<String>(selectedTags);
    }

    public Set<String> restoreDefaultTags()
    {
        if (selectedTags!=null)
            selectedTags.clear();
        else
            selectedTags = new HashSet<String>();

        selectedTags.addAll(Arrays.asList(punkTags.getWorkingTags()));
        saveSelectedTags(selectedTags);
        return new HashSet<String>(selectedTags);
    }

    /**
     * Devuelve la lista de tags disponibles, ordenadas por orden alfabético
     * @return lista de tags ordenadas por orden alfabético.
     */
    public String[] getAvailableTags()
    {
        List<String> availableTags = new ArrayList<String>();
        availableTags.addAll(Arrays.asList(punkTags.getWorkingTags()));
        availableTags.addAll(Arrays.asList(punkTags.getNotDefaultTags()));
        Collections.sort(availableTags);
        return availableTags.toArray(new String[availableTags.size()]);
    }

    /**
     * Devuelve el listado de tags con la almuadilla para que el usuario pueda buscar por tags en el
     * buscador de grupos.
     * @return
     */
    public String[] getAvailableTagsWidthAlm()
    {
        String[] availableTags = getAvailableTags();
        String[] array = new String[availableTags.length];
        int i=0;
        for (String tag : availableTags)
        {
            array[i]="#"+tag;
            i++;
        }
        return array;
    }


}
