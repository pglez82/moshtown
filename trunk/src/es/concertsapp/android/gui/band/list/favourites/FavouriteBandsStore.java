package es.concertsapp.android.gui.band.list.favourites;

import android.content.Context;
import android.widget.BaseAdapter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import es.concertsapp.android.conf.ConfValues;
import es.concertsapp.android.utils.MyApplication;
import es.lastfm.api.connector.dto.ArtistDTO;

/**
 * Esta clase almacena la lista de artistas favoritos de la aplicación. La salva y cargar de disco.
 *
 * Una funcionalidad extra que tienen esta clase es que lanza un hilo para buscar que conciertos
 * tiene cada grupo y saber si hay conciertos cerca de  nuestra ubicación.
 */
public class FavouriteBandsStore
{
    //Lista de bandas favoritas
    private static FavouriteBandsStore singleton;
    private List<ArtistDTO> favouriteBands;
    private Context context;
    private List<BaseAdapter> notifyToAdapters= new ArrayList<BaseAdapter>();
    private LookForNearEvents lookForNearEvents = new LookForNearEvents();
    private BandFavoritesFragment favouritesActivity;

    private FavouriteBandsStore()
    {
        this.context = MyApplication.getAppContext();
        loadFavouriteBands();

    }

    /**
     * Una vez que se acaban de cargar los eventos, lanzamos un hilo que vaya buscando los conciertos
     * recientes de cada grupo. Esto se mostrará en el listado una vez que aparezca.
     */
    public void startNearEventBandSearch()
    {
        if (!favouriteBands.isEmpty())
        {
            lookForNearEvents.lookForNearEvents(context,favouritesActivity,this,favouriteBands);
        }
    }

    public static FavouriteBandsStore getInstance(BandFavoritesFragment favouritesActivit)
    {
        if (singleton==null)
        {
            singleton = new FavouriteBandsStore();
        }

        if (favouritesActivit!=null)
            singleton.favouritesActivity = favouritesActivit;

        return singleton;
    }

    public synchronized List<ArtistDTO> getFavouriteBands()
    {
        return favouriteBands;
    }

    public void addAdapterToNotify(BaseAdapter adapter)
    {
        notifyToAdapters.add(adapter);
    }

    /**
     * Añade a disco bandas favoritas
     * @param artistDTO
     */
    public synchronized void addFavouriteBand(ArtistDTO artistDTO)
    {
        favouriteBands.add(artistDTO);
        notifyAdapters();
        saveFavouriteBandsToDisk();
        //Creamos una lista de solo uno para buscar solo de este
        List<ArtistDTO> temp = new ArrayList<ArtistDTO>(1);
        temp.add(artistDTO);
        lookForNearEvents.lookForNearEvents(context,favouritesActivity,this,temp);
    }

    public synchronized void removeFavouriteBand(ArtistDTO artistDTO)
    {
        favouriteBands.remove(artistDTO);
        notifyAdapters();
        saveFavouriteBandsToDisk();
    }

    public void notifyAdapters()
    {
        for (BaseAdapter baseAdapter : notifyToAdapters)
        {
            baseAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Salva a disco las bandas favoritas
     */
    public void saveFavouriteBandsToDisk()
    {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(ConfValues.FILENAME_FAVORITES, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(favouriteBands);
            oos.close();
            fos.close();
        } catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Carga de disco las bandas favoritas
     */
    public void loadFavouriteBands()
    {
        try
        {
            FileInputStream fis = context.openFileInput(ConfValues.FILENAME_FAVORITES);
            ObjectInputStream is = new ObjectInputStream(fis);
            favouriteBands = (ArrayList<ArtistDTO>) is.readObject();
            is.close();
            fis.close();
        }
        catch (Throwable e)
        {
            e.printStackTrace();

        }
        finally
        {
            if (favouriteBands==null)
            {
                favouriteBands = new ArrayList<ArtistDTO>();
            }
        }
    }
}