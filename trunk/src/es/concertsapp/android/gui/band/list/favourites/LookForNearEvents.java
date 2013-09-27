package es.concertsapp.android.gui.band.list.favourites;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import es.concertsapp.android.conf.ConfValues;
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
    private BandFavoritesFragment favouritesActivity;
    private Location locationStored = null;
    private LookForNearEventsTask lookForNearEventsTask;

    private List<ArtistDTO> alreadyLookedUp=new ArrayList<ArtistDTO>();
    private Throwable backgroundError=null;

    public void lookForNearEvents(Context context, final BandFavoritesFragment favouritesActivity, final FavouriteBandsStore favouriteBandsStore, final List<ArtistDTO> artistToLookUp)
    {
        this.favouritesActivity = favouritesActivity;
        favouritesActivity.getProgressBar().setProgress(0);
        favouritesActivity.getProgressBar().setVisibility(ProgressBar.VISIBLE);
        if (locationStored==null)
        {
            try
            {
                MyLocation.getCachedLocation(context,new MyLocation.LocationResult()
                {
                    @Override
                    public void locationFound(Location location, String name)
                    {
                        if (location!=null)
                        {
                            locationStored = location;
                            lookForNearEventsTask = new LookForNearEventsTask(favouriteBandsStore,location);
                            favouritesActivity.getProgressBar().setProgress(20);
                            lookForNearEventsTask.execute(artistToLookUp);
                        }
                    }
                });
            }
            catch (Throwable e)
            {
            }
        }
        else
        {
            lookForNearEventsTask = new LookForNearEventsTask(favouriteBandsStore,locationStored);
            lookForNearEventsTask.execute(artistToLookUp);
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
                List<ArtistDTO> favouriteBands = params[0];
                LastFmApiConnector lastFmApiconnector = LastFmApiConnectorFactory.getInstance();
                int number = favouriteBands.size();
                int proccessed = 0;
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
                            if (distance< ConfValues.NEAR_EVENT_DISTANCE)
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

                    //Actualizamos la barra d eprogreso
                    favouritesActivity.getProgressBar().setProgress((((++proccessed)*80)/number)+20);
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
            favouritesActivity.getProgressBar().setVisibility(ProgressBar.INVISIBLE);
            if (backgroundError!=null)
                UnexpectedErrorHandler.handleUnexpectedError(favouritesActivity.getActivity(),backgroundError);
        }


    }
}