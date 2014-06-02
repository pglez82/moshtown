package es.concertsapp.android.gui.band.list.favourites;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import es.concertsapp.android.conf.ConfValues;
import es.concertsapp.android.gui.R;
import es.concertsapp.android.utils.DialogUtils;
import es.concertsapp.android.utils.LastFmApiConnectorFactory;
import es.concertsapp.android.utils.UnexpectedErrorHandler;
import es.concertsapp.android.utils.geo.DistanceCalculator;
import es.concertsapp.android.utils.geo.MyLocation;
import es.lastfm.api.connector.LastFmApiConnector;
import es.lastfm.api.connector.dto.ArtistDTO;
import es.lastfm.api.connector.dto.ArtistEventDTO;

/**
 * Created by pablo on 26/08/13.
 */
public class LookForNearEvents
{
    private static final String LOG_TAG="LOOKFORNEAREVENTS";
    private BandFavoritesFragment favoritesFragment;
    private LookForNearEventsTask lookForNearEventsTask;

    //Este timer está creado para borrar la lista de artistas ya buscados cada x minutos
    private Timer timer;

    private List<ArtistDTO> alreadyLookedUp=Collections.synchronizedList(new ArrayList());
    private Throwable backgroundError=null;

    public LookForNearEvents()
    {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                alreadyLookedUp.clear();
            }
        },0,ConfValues.TIME_FAVOURITE_CONCERTS_EXPIRES*1000*60);
    }




    public void lookForNearEvents(Context context, final BandFavoritesFragment favouritesActivity, final FavouriteBandsStore favouriteBandsStore, final List<ArtistDTO> artistToLookUp)
    {
        this.favoritesFragment = favouritesActivity;
        //Mostramos la barra de progreso
        favoritesFragment.setProgressBarState(View.VISIBLE);

        MyLocation.LocationResult locationResult = new MyLocation.LocationResult()
        {
            @Override
            public void locationFound(final Location location,final String name)
            {
                if (favouritesActivity!=null && favouritesActivity.getActivity()!=null)
                {
                    favouritesActivity.getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (location!=null)
                            {
                                lookForNearEventsTask = new LookForNearEventsTask(favouriteBandsStore,location);
                                lookForNearEventsTask.execute(artistToLookUp);
                            }
                            else
                            {
                                favoritesFragment.setProgressBarState(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        };

        try
        {
            if (!MyLocation.getCachedLocation(context,locationResult))
            {
                favoritesFragment.setProgressBarState(View.INVISIBLE);
                if (favouritesActivity!=null)
                    DialogUtils.showToast(favouritesActivity.getActivity(),2500, R.string.toastgpsenable);
            }
        }
        catch (Throwable e)
        {
            //Todo: Revisar si este throawle aquí es necesario porque me estoy comiendo todas las excepciones
        }
    }

    /**
     * Este asynctask busca eventos cercanos de los grupos que son favoritos. Solo busca los que le
     * pasamos en la lista. Almacena una especie de caché (hasta que se sale de la aplicación), porque
     * se supone que esta info no va a cambiar durante una ejecución de la app.
     */
    private  class LookForNearEventsTask  extends AsyncTask<List<ArtistDTO>, Void, Void>
    {
        private FavouriteBandsStore favouriteBandsStore;
        private Location location;

        public LookForNearEventsTask(FavouriteBandsStore favouriteBandsStore, Location location)
        {
            this.favouriteBandsStore = favouriteBandsStore;
            this.location = location;

        }

        @Override
        protected Void doInBackground(final List<ArtistDTO>... params)
        {
            try
            {
                //Creo una lista nueva para evitar tener que sincronizarme con la otra
                List<ArtistDTO> favouriteBands = new ArrayList<ArtistDTO>(params[0]);
                LastFmApiConnector lastFmApiconnector = LastFmApiConnectorFactory.getInstance();
                for (ArtistDTO artistDTO : favouriteBands)
                {
                    int index=alreadyLookedUp.indexOf(artistDTO);
                    if (index<0)
                    {
                        artistDTO.setNearEvents(false);
                        List<ArtistEventDTO> listArtistEventDTO = lastFmApiconnector.getArtistEvents(artistDTO.getArtistName());
                        for (ArtistEventDTO artistEventDTO : listArtistEventDTO)
                        {
                            double distance = DistanceCalculator.distance(location.getLatitude(), location.getLongitude(), artistEventDTO.getLatEventPlace(), artistEventDTO.getLonEventPlace());
                            if (distance< ConfValues.getIntConfigurableValue(favoritesFragment.getActivity(),ConfValues.ConfigurableValue.EVENT_RATIO_DISTANCE))
                            {
                                artistDTO.setNearEvents(true);
                                publishProgress();
                                break;
                            }
                        }
                        alreadyLookedUp.add(artistDTO);
                    }
                    else
                    {
                        artistDTO.setNearEvents(alreadyLookedUp.get(index).isNearEvents());
                        publishProgress();
                    }
                }
            }
            catch (Throwable e)
            {
                Log.e(LOG_TAG, "Se ha producido un error al buscar los eventos cercanos de los artistas favoritos");
                backgroundError=e;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            super.onProgressUpdate(values);
            favouriteBandsStore.notifyAdapters();
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            favoritesFragment.setProgressBarState(View.INVISIBLE);
            if (backgroundError!=null)
                UnexpectedErrorHandler.handleUnexpectedError(favoritesFragment.getActivity(),backgroundError);
        }


    }
}