package es.concertsapp.android.gui.event.list;


import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import es.concertsapp.android.conf.ConfValues;
import es.concertsapp.android.gui.R;
import es.concertsapp.android.utils.DialogUtils;
import es.concertsapp.android.utils.LastFmApiConnectorFactory;
import es.concertsapp.android.utils.UnexpectedErrorHandler;
import es.concertsapp.android.utils.images.ImageDownloader;
import es.lastfm.api.connector.LastFmApiConnector;
import es.lastfm.api.connector.NewEventAvaibleListener;
import es.lastfm.api.connector.PagedProcesedListener;
import es.lastfm.api.connector.dto.EventDTO;


public class EventPageAdapter extends BaseAdapter
{
    private EventListActivity eventListActivity;
    private EventListActivityRetained eventListActivityRetained;
	private int itemRowResID;
    static final String LOG_TAG = "PAGEADAPTER";

    //De aquí van a salir nuestros datos
    private LastFmApiConnector lastFmApiConnector;
    private List<EventDTO> listEvents;
    private String ciudad;
    private double lat,lon;
    private boolean coordinates;

    private EventListHelper eventListHelper;

    //Variable que indica si se produjo algún error (si !=null) en el proceso en segundo plano. On background
    //no puede mostrar el error porque no puede tocar la ui. Eso hay que hacerlo en el postexecute
    private Throwable backgroundError=null;

    //Hilo que carga los conciertos
    private LoadingTask loadingTask;
    
    //Número de eventos que cargamos de una tacada
	private final int EVENTS_PER_PAGE = 8;
	
	//Se define si se busca dentro de las etiquetas de los artistas en el caso de que el evento no tenga etiquetas
	private static boolean LOOK_ARTISTS_TAGS = true;
	
	//Cuantas tacadas llevamos cargadas
	private int threadExecutionCounter = 0;
    
    //Indica si actualmente se están cargando nuevos eventos o no
    //private boolean loading = false;
    
    //Para bajar las imagenes de manera asincroncas
    private final ImageDownloader imageDownloader;

    private class LoadingTask extends AsyncTask<Void, EventDTO, Boolean> implements NewEventAvaibleListener, PagedProcesedListener
    {
    	@Override
    	protected void onPreExecute()
    	{
            backgroundError = null;
    		if (!isCancelled())
            {
    			lastFmApiConnector.setEventSearchListeners(this, this);
                eventListActivityRetained.showElement(EventListActivityRetained.ListElementsOnlyOneVisible.LOADING);
            }
    	}
    	
		@Override
		protected Boolean doInBackground(Void... params) 
		{
			threadExecutionCounter++;
			Log.d(LOG_TAG, "Arrancando el hilo para buscar los eventos...");
			try
        	{
                final int eventRatiodistance = ConfValues.getIntConfigurableValue(eventListActivity,ConfValues.ConfigurableValue.EVENT_RATIO_DISTANCE);
        		do
        		{

        			Log.d(LOG_TAG, "Iniciando llamada al api");
        			if (coordinates)
        			{
        				Log.d(LOG_TAG,"Latitud:"+lat+" Longitud:"+lon);
        				lastFmApiConnector.setLatLon(lat, lon, eventRatiodistance,eventRatiodistance*4);
        				lastFmApiConnector.listPagedEventsFilteredByTags(LOOK_ARTISTS_TAGS);
        			}
        			else
        			{
        				Log.d(LOG_TAG,"ciudad:"+ciudad);
        				lastFmApiConnector.setLocation(ciudad,eventRatiodistance,eventRatiodistance*4);
        				lastFmApiConnector.listPagedEventsFilteredByTags(LOOK_ARTISTS_TAGS);
        			}
        			Log.d(LOG_TAG, "Api retornada:" + listEvents.size() + "/" + EVENTS_PER_PAGE*threadExecutionCounter);
        		}
        		while ((listEvents.size() <= (EVENTS_PER_PAGE*threadExecutionCounter)) && lastFmApiConnector.hasMoreResults() && !isCancelled());
        		/*
        		 * El número de conciertos por página no es constante. Es decir, cuando se llama a listPagedEventsFilteredByTags esta
        		 * función analiza muchos más eventos d elos que muestra (solo del estilo que estemos trabajando) y va 
        		 * llamando al listener (unas cuantas veces por llamada a la función). En este punto comprobamos si hemos pasado
        		 * el límite que tenemos programado por página y sino lanzamos otra llamada. Por eso el número puede ser variable.
        		 */
        	}
        	catch (Throwable e) {
        		Log.e(LOG_TAG, "Error en la llamada al api...",e);
                backgroundError = e;
        	}
			if (lastFmApiConnector.hasMoreResults())
				return true;
			else
				return false;
		}

		@Override
		public void newElementAvailable(EventDTO event) 
		{
			Log.d(LOG_TAG, "Nos ha llegado un nuevo evento..."+event.getEventTitle());
			this.publishProgress(event);
		}

        @Override
        public void pagedProcessed(int page, int totalPages, int nuevos, int totalEventsFound, int distance)
        {
            final int eventRatiodistance = ConfValues.getIntConfigurableValue(eventListActivity,ConfValues.ConfigurableValue.EVENT_RATIO_DISTANCE);
            if (totalEventsFound==0 && distance==eventRatiodistance && !lastFmApiConnector.hasMoreResults())
            {
                if (eventListActivity!=null)
                {
                    eventListActivity.runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            DialogUtils.showToast(eventListActivity, Toast.LENGTH_LONG,R.string.no_concerts_near);
                        }
                    });
                }
            }
        }

        protected void onProgressUpdate(EventDTO... progress)
		{
			if (!isCancelled())
			{
		         listEvents.add(progress[0]);
		         notifyDataSetChanged();
			}
	    }
		
		
		@Override
		protected void onPostExecute(Boolean result)
		{
            if (backgroundError != null)
            {
                UnexpectedErrorHandler.handleUnexpectedError(eventListActivity,backgroundError);
            }
			if (!isCancelled())
			{
				Log.d(LOG_TAG,"El hilo ha acabado por ahora..., actualizando el footer");
                eventListActivityRetained.hideAllElements();
				if (result)
				{
					Log.d(LOG_TAG,"Todavía hay más posibles resultados así que sacamos el boton en el footer");
					//eventListActivityRetained.showFooter(EventListActivityRetained.ListFooters.LOAD_MORE);
                    eventListActivityRetained.showElement(EventListActivityRetained.ListElementsOnlyOneVisible.MORE_RESULTS);
				}
			}

            //Si no hay ningún evento lo indicamos
            if (listEvents.isEmpty())
            {
                eventListActivityRetained.showElement(EventListActivityRetained.ListElementsOnlyOneVisible.NO_RESULTS);
            }
		}
		
		/*
		 * Esto se ejecuta cuando el hilo se cancela (se para la busqueda)
		 * Normalmente solo pasa esto cuando se vuelve a pulsar buscar sin que
		 * acabe una búsqueda en cuestión
		 * @see android.os.AsyncTask#onCancelled()
		 */
		@Override
	    protected void onCancelled() 
		{
			//Aquí podemos poner el botón de buscar otra vez activo y el edit para escribir
	        Log.d(LOG_TAG,"El hilo de busqueda de conciertos ha sido cancelado."); 
		}
		
    }  
    
    public EventPageAdapter(EventListActivity eventListActivity, EventListActivityRetained eventListActivityRetained, int itemRowResID)
    { 
        this.eventListActivity = eventListActivity;
        this.eventListActivityRetained = eventListActivityRetained;
		this.itemRowResID = itemRowResID;
        listEvents = new ArrayList<EventDTO>();
        imageDownloader=ImageDownloader.getInstance();
        eventListHelper = new EventListHelper(eventListActivity);
    }

    public void setEventListActivity(EventListActivity context)
    {
        this.eventListActivity = context;
    }

    /*
         * Cancela la busqueda actual si existe y luego lanza la nueva
         */
    private void cancelCurrentSearch()
    {
        if (loadingTask!=null)
        {
            loadingTask.cancel(true);
        }

        listEvents = new ArrayList<EventDTO>();
        threadExecutionCounter=0;
        notifyDataSetChanged();
    }
    
    private void startEventSearch()
    {
    	//Borramos la cache de las imagenes si había ya descargadas
    	imageDownloader.clearCache();
    	lastFmApiConnector =  LastFmApiConnectorFactory.getNewInstance();

        //Si ya existe una búsqueda haciendose hay que cancelarla
        cancelCurrentSearch();
        loadingTask = new LoadingTask();
        loadingTask.execute();
        	
    }
    
    public void startEventSearch(String ciudad)
    {
    	Log.d(LOG_TAG, "Inicializando aplicacion de busqueda por ciudad...");
    	//Inicializamos el API de lastfm
    	this.ciudad = ciudad;
    	this.coordinates=false;
        startEventSearch();   
    }
    
    public void startEventSearch(double lat, double lon)
    {
    	Log.d(LOG_TAG, "Inicializando aplicacion de busqueda por lat y lon...");
    	//Inicializamos el API de lastfm
    	this.lat=lat;
    	this.lon=lon;
    	this.coordinates=true;
    	startEventSearch();
    }
    
    public void continueEventSearch()
    {
    	new LoadingTask().execute();
    }

    public int getCount() 
    {
    	synchronized (listEvents) 
    	{
    		return listEvents.size();
		}
        
    }

    public Object getItem(int position) 
    {
    	synchronized (listEvents) 
    	{
    		return listEvents.get(position);
		}
        
    }

    public long getItemId(int position) {  
        return position;
    }

    /*Este método se llama por cada elemento de la lista
     * en position viene el elemento que nos pide
     */
    
    public View getView(int position, View convertView, ViewGroup parent) 
    {
    	EventListHelper.EventHolder holder;
    	if (convertView == null)
    	{
	        LayoutInflater inflater = LayoutInflater.from(eventListActivity);
			convertView = inflater.inflate(itemRowResID, parent, false);
			
			holder = new EventListHelper.EventHolder();
			holder.concertListInfo=(TextView)convertView.findViewById(R.id.concertlistinfo);
		    holder.placeListInfo=(TextView)convertView.findViewById(R.id.placelistinfo);
		    holder.eventDate =(TextView)convertView.findViewById(R.id.eventDate);
			convertView.setTag(holder);
    	}
    	else
    	{
			holder = (EventListHelper.EventHolder) convertView.getTag();
		}
		
       
    	//Mostramos el elemento
        EventDTO event = (EventDTO)this.getItem(position);
        eventListHelper.loadInfoEvent(eventListActivity,holder,event);
        return convertView;
    }

    public boolean areAllItemsEnabled() {
        return true;
    }

    public boolean  isEnabled(int position) {
        return true;
    }
}
