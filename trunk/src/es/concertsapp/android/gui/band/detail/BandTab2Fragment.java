package es.concertsapp.android.gui.band.detail;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import es.concertsapp.android.component.LastFmImageView;
import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.event.detail.EventInfoActivity;
import es.concertsapp.android.utils.LastFmApiConnectorFactory;
import es.concertsapp.android.utils.MyAppParameters;
import es.concertsapp.android.utils.MyApplication;
import es.concertsapp.android.utils.UnexpectedErrorHandler;
import es.concertsapp.android.utils.date.DateFormater;
import es.lastfm.api.connector.LastFmApiConnector;
import es.lastfm.api.connector.dto.ArtistDTO;
import es.lastfm.api.connector.dto.ArtistEventDTO;
import es.lastfm.api.connector.dto.EventDTO;
import es.lastfm.api.connector.exception.LastFmException;

public class BandTab2Fragment extends Fragment 
{
    private static final String LOG_TAG="BANDTAB2FRAGMENT";
    private ArtistDTO artistDTO;
    private Throwable backgroundError=null;
    private ProgressBar eventsProgressBar;
    private DonwloadEventsBandTask downDonwloadEventsBandTask;
	
	static class ArtistEventHolder 
	{
		TextView bandconcertlistinfo;
		TextView bandplacelistinfo;
		TextView banddatelistinfo;
	}

    public BandTab2Fragment()
    {
        super();
    }

    public BandTab2Fragment(ArtistDTO artistDTO)
    {
        super();
        this.artistDTO = artistDTO;
    }

    /*Creo que esto está bastante bien como ejemplo para cargar un listado con un hilo
         * El propio hilo tiene dentro un adapter y cuando el hilo acaba, crea el adapter
         * y se lo asigna a la lista.
         */
	private class DonwloadEventsBandTask extends AsyncTask<String, Void, List<ArtistEventDTO>>
	{	
		//Donde se van a cargar los datos
		private ListView listView;
        private BandEventsAdapter bandEventsAdapter;

        public synchronized void updateListView(ListView listView)
        {
            this.listView = listView;
            this.listView.setAdapter(bandEventsAdapter);
            this.listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {
                    Intent i = new Intent(getActivity(),EventInfoActivity.class);

                    ArtistEventDTO bandEvent = (ArtistEventDTO)bandEventsAdapter.getItem(position-1);
                    i.putExtra(MyAppParameters.EVENTID, bandEvent.getEventId());
                    startActivity(i);
                }
            });
        }
		
		//Adapter para mostrar los datos cargados por este hilo
		private class BandEventsAdapter extends BaseAdapter
		{	
			private List<ArtistEventDTO> listEvents;
			
			public BandEventsAdapter(List<ArtistEventDTO> listEvents)
			{
				this.listEvents = listEvents;
			}

			@Override
			public int getCount() 
			{
				synchronized (listEvents) 
		    	{
					return listEvents.size();
				}
			}

			@Override
			public Object getItem(int position) 
			{
				synchronized (listEvents) 
		    	{
		    		return listEvents.get(position);
				}	
			}

			@Override
			public long getItemId(int position) 
			{
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) 
			{
				View row = convertView;
				ArtistEventHolder holder;

				if (row == null) {
					LayoutInflater inflater = (getActivity()).getLayoutInflater();
					row = inflater.inflate(R.layout.item_band_event_row, parent, false);

					holder = new ArtistEventHolder();
					holder.bandconcertlistinfo = (TextView)row.findViewById(R.id.bandconcertlistinfo);
					holder.bandplacelistinfo = (TextView) row.findViewById(R.id.bandplacelistinfo);
					holder.banddatelistinfo = (TextView) row.findViewById(R.id.banddatelistinfo);
					row.setTag(holder);
				} else {
					holder = (ArtistEventHolder) row.getTag();
				}

				ArtistEventDTO artistEventDTO = (ArtistEventDTO)this.getItem(position);
				holder.bandconcertlistinfo.setText(artistEventDTO.getEventTitle());
				holder.banddatelistinfo.setText(DateFormater.getInstance(MyApplication.getLocale()).formatDate(artistEventDTO.getStartDate()));
				holder.bandplacelistinfo.setText(artistEventDTO.getEventPlace());

				return row;
			}
		}
		
		public DonwloadEventsBandTask(ListView listView)
		{
			this.listView = listView;
		}

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            eventsProgressBar.setVisibility(View.VISIBLE);
            backgroundError=null;
        }

        @Override
		protected List<ArtistEventDTO> doInBackground(String... params) 
		{
			LastFmApiConnector lastFmApiConnector = LastFmApiConnectorFactory.getInstance();
            List<ArtistEventDTO> listArtist=null;
            try
            {
			    listArtist=lastFmApiConnector.getArtistEvents(params[0]);
            }
            catch (LastFmException e)
            {
                Log.e(LOG_TAG,"Se ha producido un error obteniendo los eventos de un artista",e);
                backgroundError = e;
            }
            return listArtist;
		}

		@Override
		protected void onPostExecute(final List<ArtistEventDTO> result) 
		{
            eventsProgressBar.setVisibility(View.INVISIBLE);
            if (backgroundError!=null)
                UnexpectedErrorHandler.handleUnexpectedError(getActivity(),backgroundError);
            else
            {
                bandEventsAdapter = new BandEventsAdapter(result);
                updateListView(listView);
            }
		}
	}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
		View rootView = inflater.inflate(R.layout.activity_band_info_t2,container, false);
        Bundle args = getArguments();
        String artistName = args.getString(MyAppParameters.BANDID);

        try
        {
            if (artistDTO==null)
            {

                LastFmApiConnector lastFmApiConnector = LastFmApiConnectorFactory.getInstance();
                artistDTO = lastFmApiConnector.getArtistInfo(artistName);
            }
            ((TextView) rootView.findViewById(R.id.detailedbandname)).setText(artistName);

            // Cargamos la imagen
            LastFmImageView artistImageView = (LastFmImageView)rootView.findViewById(R.id.detailedbandimage);
            artistImageView.setLastFmImageSource(artistDTO);
            //ImageView artistImageView = (ImageView) rootView.findViewById(R.id.detailedbandimage);
            //ImageDownloader imageDownloader = ImageDownloader.getInstance();
            //imageDownloader.download(artistDTO.getImageURL(ImageSize.LARGE),artistImageView);

            // Cargamos los eventos del artista
            ListView listView = (ListView) rootView.findViewById(R.id.detailedbandlistevents);
            View header = inflater.inflate(R.layout.list_band_events_header,null);
            listView.addHeaderView(header,null,false);
            header.setVisibility(View.VISIBLE);

            eventsProgressBar = (ProgressBar) rootView.findViewById(R.id.progressbareventsband);

            if (downDonwloadEventsBandTask==null)
            {
                downDonwloadEventsBandTask = new DonwloadEventsBandTask(listView);
                downDonwloadEventsBandTask.execute(artistName);
            }
            else
            {
                downDonwloadEventsBandTask.updateListView(listView);
            }
        }
        catch (Throwable e)
        {
            Log.e(LOG_TAG, "Se ha producido un error al obtener la info detallada del artista");
            UnexpectedErrorHandler.handleUnexpectedError(this.getActivity(),e);
        }
		return rootView;
	}

    @Override
    public void onDestroy()
    {
        Log.d(LOG_TAG,"Destruido el fragmento");
        if (downDonwloadEventsBandTask!=null)
        {
            downDonwloadEventsBandTask.cancel(true);
        }
        super.onDestroy();
    }
}
