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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import es.concertsapp.android.component.LastFmImageView;
import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.event.detail.EventInfoActivity;
import es.concertsapp.android.gui.event.list.EventListHelper;
import es.concertsapp.android.gui.footer.FooterLayoutUtils;
import es.concertsapp.android.gui.legal.LegalConditionsActivity;
import es.concertsapp.android.gui.legal.MoshTownConditionsActivity;
import es.concertsapp.android.utils.LastFmApiConnectorFactory;
import es.concertsapp.android.utils.MyAppParameters;
import es.concertsapp.android.utils.UnexpectedErrorHandler;
import es.concertsapp.android.utils.font.FontUtils;
import es.lastfm.api.connector.LastFmApiConnector;
import es.lastfm.api.connector.dto.ArtistDTO;
import es.lastfm.api.connector.dto.ArtistEventDTO;
import es.lastfm.api.connector.exception.LastFmException;

public class BandTab2Fragment extends Fragment 
{
    private static final String LOG_TAG="BANDTAB2FRAGMENT";
    private ArtistDTO artistDTO;
    private Throwable backgroundError=null;
    private ProgressBar eventsProgressBar;
    private int eventsProgressBarState = View.INVISIBLE;
    private DonwloadEventsBandTask downDonwloadEventsBandTask;

    private EventListHelper eventListHelper;

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
        private int noElementsVisibility = View.INVISIBLE;

        public synchronized void updateListView(ListView listView)
        {
            this.listView = listView;
            this.listView.setAdapter(bandEventsAdapter);
            this.listView.getEmptyView().setVisibility(noElementsVisibility);
            this.listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {
                    Intent i = new Intent(getActivity(),EventInfoActivity.class);

                    ArtistEventDTO bandEvent = (ArtistEventDTO)bandEventsAdapter.getItem(position);
                    i.putExtra(MyAppParameters.EVENTID, bandEvent.getEventId());
                    startActivity(i);
                }
            });
        }

        public synchronized void setNoElementsVisibility(int visibility)
        {
            noElementsVisibility = visibility;
            listView.getEmptyView().setVisibility(noElementsVisibility);
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
				EventListHelper.EventHolder holder;

				if (row == null) {
					LayoutInflater inflater = (getActivity()).getLayoutInflater();
					row = inflater.inflate(R.layout.item_row, parent, false);

					holder = new EventListHelper.EventHolder();
                    holder.concertListInfo=(TextView)row.findViewById(R.id.concertlistinfo);
                    holder.placeListInfo=(TextView)row.findViewById(R.id.placelistinfo);
                    holder.eventDate =(TextView)row.findViewById(R.id.eventDate);
					row.setTag(holder);
				} else {
					holder = (EventListHelper.EventHolder) row.getTag();
				}

				ArtistEventDTO artistEventDTO = (ArtistEventDTO)this.getItem(position);
                eventListHelper.loadInfoEvent(getActivity(),holder,artistEventDTO);
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
            setProgressBarVisibility(View.VISIBLE);
            setNoElementsVisibility(View.INVISIBLE);
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
            setProgressBarVisibility(View.INVISIBLE);
            if (backgroundError!=null)
                UnexpectedErrorHandler.handleUnexpectedError(getActivity(),backgroundError);
            else
            {
                bandEventsAdapter = new BandEventsAdapter(result);
                if (result==null || result.isEmpty())
                    setNoElementsVisibility(View.VISIBLE);
                updateListView(listView);
            }
		}
	}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        eventListHelper = new EventListHelper(getActivity());
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
            TextView  detailedBandName = ((TextView) rootView.findViewById(R.id.detailedbandname));
            detailedBandName.setText(artistName);

            // Cargamos la imagen
            LastFmImageView artistImageView = (LastFmImageView)rootView.findViewById(R.id.detailedbandimage);
            artistImageView.setLastFmImageSource(artistDTO);

            // Cargamos los eventos del artista
            ListView listView = (ListView) rootView.findViewById(R.id.detailedbandlistevents);
            TextView emptyView = (TextView) rootView.findViewById(R.id.listsimilarnoresults);
            listView.setEmptyView(emptyView);
            eventsProgressBar = (ProgressBar) rootView.findViewById(R.id.progressbarcommon);
            eventsProgressBar.setVisibility(eventsProgressBarState);

            TextView nextshowstitle = (TextView)rootView.findViewById(R.id.nextshowstitle);

            FooterLayoutUtils.initializeButtonsFunctions(getActivity(),rootView);

            //Establecemos las fuentes
            FontUtils.setRobotoFont(getActivity(), detailedBandName, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
            FontUtils.setRobotoFont(getActivity(), nextshowstitle, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
            FontUtils.setRobotoFont(getActivity(),rootView.findViewById(R.id.nextshowstitle),FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
            FontUtils.setRobotoFont(getActivity(),emptyView,FontUtils.FontType.ROBOTOCONDENSED_LIGHT);

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

    public void setProgressBarVisibility(int visibility)
    {
        this.eventsProgressBarState = visibility;
        eventsProgressBar.setVisibility(eventsProgressBarState);
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
