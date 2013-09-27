package es.concertsapp.android.gui.event.list;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.concertsapp.android.conf.ConfValues;
import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.event.detail.EventInfoActivity;
import es.concertsapp.android.gui.menu.MenuFragmentActivity;
import es.concertsapp.android.utils.DialogUtils;
import es.concertsapp.android.utils.MyAppParameters;
import es.concertsapp.android.utils.geo.LatitudeLongitude;
import es.concertsapp.android.utils.geo.MyLocation;
import es.concertsapp.android.utils.geo.MyLocation.LocationResult;
import es.concertsapp.android.utils.geo.PlaceInterface;
import es.lastfm.api.connector.dto.EventDTO;

public class EventListActivity extends MenuFragmentActivity
{
    static final String LOG_TAG = "EVENTLISTACTIVITY";
    private PlacesAutoCompleteAdapter placesAutoCompleteAdapter;

    private EventListActivityRetained eventListActivityRetained;

    //Elemento de cargando... en el footer
    private View loadingFooter;
    private View noResultsFooter;
    //Elemento de cargar más en el footer
    private View buttonMoreFooter;

    //Lista de lugares ya buscados
    private List<PlaceInterface> listPlacesSearched;

    private LatitudeLongitude latlonSearch;


    
    /*
     * Atención, en el emulador siempre a false porque no funciona.
     * Habrá que probar que pasa en dispositivos reales.
     */
    private static final boolean USE_GEOCODER_DESAMBIGUATION=true;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list_main);

        FragmentManager fm = getSupportFragmentManager();
        eventListActivityRetained = (EventListActivityRetained) fm.findFragmentByTag("eventlistfragment");

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (eventListActivityRetained == null) {
            eventListActivityRetained = new EventListActivityRetained();
            fm.beginTransaction().add(eventListActivityRetained, "eventlistfragment").commit();
        }

        loadingFooter = getLayoutInflater().inflate(R.layout.loading_row, null);
        buttonMoreFooter = getLayoutInflater().inflate(R.layout.buttonmore_layout,null);
        noResultsFooter = getLayoutInflater().inflate(R.layout.no_results_footer,null);

        getListView().addFooterView(loadingFooter,null,false);
        getListView().addFooterView(buttonMoreFooter);
        getListView().addFooterView(noResultsFooter,null,false);

        noResultsFooter.setEnabled(false);
        loadingFooter.setEnabled(false);
        loadingFooter.setVisibility(View.GONE);
        buttonMoreFooter.setVisibility(View.GONE);
        noResultsFooter.setVisibility(View.GONE);

        getListView().setAdapter(eventListActivityRetained.getEventPageAdapter());
        getListView().removeFooterView(loadingFooter);
        getListView().removeFooterView(buttonMoreFooter);
        getListView().removeFooterView(noResultsFooter);
        //Listener en los elementos de la lista para ir al detalle del evento
        getListView().setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                Intent i = new Intent(EventListActivity.this, EventInfoActivity.class);
                EventDTO eventDTO = (EventDTO) eventListActivityRetained.getEventPageAdapter().getItem(position);
                i.putExtra(MyAppParameters.EVENTID, eventDTO.getEventId());
                startActivity(i);
            }
        });
        ImageButton buscarButon = (ImageButton)findViewById(R.id.buscarButon);
        buscarButon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                botonBuscar();
            }
        });

        final AutoCompleteTextView searchTextView = (AutoCompleteTextView)findViewById(R.id.editCiudad);

        ImageButton positionButton = (ImageButton)findViewById(R.id.positionButon);
        positionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getPosition(v);
            }
        });


        if (USE_GEOCODER_DESAMBIGUATION)
        {
            loadListPlacesSearched();
            placesAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this,R.layout.city_list_result_layout,listPlacesSearched);
            searchTextView.setAdapter(placesAutoCompleteAdapter);
            //Listener cuando se selecciona un elemento de la lista desplegable de lugares
            searchTextView.setOnItemClickListener(new OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                {
                    searchTextView.dismissDropDown();
                    PlaceInterface place = placesAutoCompleteAdapter.getItem(i);
                    latlonSearch = place.getLatLon();
                    eventListActivityRetained.getEventPageAdapter().startEventSearch(latlonSearch.lat, latlonSearch.lon);
                    //Salvamos la lista de sitios buscados
                    latlonSearch=null;
                    saveListPlacesSearched(place);
                }
            });
        }

        //Listener del botón de borrar
        ImageButton imageButton = (ImageButton)findViewById(R.id.buttonClear);
        imageButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                searchTextView.setText("");
                latlonSearch=null;
            }
        });



        searchTextView.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    if (keyCode==KeyEvent.KEYCODE_ENTER)
                    {
                        searchTextView.dismissDropDown();
                        botonBuscar();
                        return true;
                    }
                }
                return false;
            }
        });


    }


    private ListView getListView()
    {
        return (ListView)findViewById(R.id.list_main_events);
    }

    /**
     * Carga de disco la lista de sitios más buscados
     */
    private void loadListPlacesSearched()
    {
        try
        {
            FileInputStream fis = openFileInput(ConfValues.FILENAME_CITIES);
            ObjectInputStream is = new ObjectInputStream(fis);
            listPlacesSearched = (List<PlaceInterface>) is.readObject();
            is.close();
            fis.close();
        }
        catch (Throwable e)
        {
            e.printStackTrace();

        }
        finally
        {
            if (listPlacesSearched==null)
            {
                listPlacesSearched = new ArrayList<PlaceInterface>();
            }
        }
        listPlacesSearched = Collections.checkedList(listPlacesSearched,PlaceInterface.class);
    }

    private void saveListPlacesSearched(final PlaceInterface place)
    {
        new Runnable()
        {
            @Override
            public void run()
            {
                FileOutputStream fos;
                try {
                    if (!listPlacesSearched.contains(place))
                    {
                        listPlacesSearched.add(0, place);
                        if (listPlacesSearched.size()>ConfValues.MAX_CITIES_STORED)
                        {
                            listPlacesSearched=listPlacesSearched.subList(0,ConfValues.MAX_CITIES_STORED-1);
                            placesAutoCompleteAdapter.setListPlacesSearched(listPlacesSearched);
                        }
                        Context context = getListView().getContext();
                        fos = context.openFileOutput(ConfValues.FILENAME_CITIES, Context.MODE_PRIVATE);
                        ObjectOutputStream oos = new ObjectOutputStream(fos);
                        oos.writeObject(listPlacesSearched);
                        oos.close();
                        fos.close();
                    }
                } catch (Throwable e)
                {
                    e.printStackTrace();
                }
            }
        }.run();
    }

	/*
     * Botón de buscar. Si está activado lo de desambiguar debería de lanzar
     * una consulta al geocoder y resolver si hay dudas
     */
    public void botonBuscar()
    {
        MyLocation.cancelSearch();
        if (USE_GEOCODER_DESAMBIGUATION && latlonSearch!=null)
        {
            eventListActivityRetained.getEventPageAdapter().startEventSearch(latlonSearch.lat, latlonSearch.lon);
        }
        else
        {
    	    final EditText text = (AutoCompleteTextView)findViewById(R.id.editCiudad);
            eventListActivityRetained.getEventPageAdapter().startEventSearch(text.getText().toString());
        }
    }


    /**
     * Obtiene la posición a partir del gps o de la red con un hilo a parte.
     * @param view
     */
    public void getPosition(final View view)
    {
    	((AutoCompleteTextView)findViewById(R.id.editCiudad)).setText("Buscando...");
    	LocationResult locationResult = new LocationResult(){
    		@Override
    	    public void locationFound(Location location,String name)
    	    {
    			//Cuidado: este handler lo llama el hilo que busca la localización
    			//Tenemos que pasar el control al hilo de vista para procesar esto
    			runOnUiThread(new ReturnFromLocation(view.getContext(), location, name));
    	    }
    	};
    	MyLocation myLocation = new MyLocation();
        //si devuelve false es que no hay gps activao ni nada
    	if (!myLocation.getLocation(this, locationResult))
        {
            DialogUtils.showErrorDialog(this,R.string.location_error);
            ((AutoCompleteTextView)findViewById(R.id.editCiudad)).setText("");
        }
    }

    public void hideAllFooters()
    {
        if (loadingFooter.getVisibility()==View.VISIBLE)
        {
            getListView().removeFooterView(loadingFooter);
            loadingFooter.setVisibility(View.GONE);
        }
        if (noResultsFooter.getVisibility()==View.VISIBLE)
        {
            getListView().removeFooterView(noResultsFooter);
            noResultsFooter.setVisibility(View.GONE);
        }
        if (buttonMoreFooter.getVisibility()==View.VISIBLE)
        {
            getListView().removeFooterView(buttonMoreFooter);
            buttonMoreFooter.setVisibility(View.GONE);
        }
    }

    public void updateFooters(EventListActivityRetained.ListFooters footer)
    {
        hideAllFooters();
        if (footer==EventListActivityRetained.ListFooters.LOADING)
        {
            getListView().addFooterView(loadingFooter,null,false);
            loadingFooter.setVisibility(View.VISIBLE);
        }
        else if (footer==EventListActivityRetained.ListFooters.NO_RESULTS)
        {
            getListView().addFooterView(noResultsFooter,null,false);
            noResultsFooter.setVisibility(View.VISIBLE);
        }
        else if (footer== EventListActivityRetained.ListFooters.LOAD_MORE)
        {
            getListView().addFooterView(buttonMoreFooter);
            buttonMoreFooter.setVisibility(View.VISIBLE);
            Button listMoreButton = (Button)findViewById(R.id.listMoreButton);
            listMoreButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    botonListMore();
                }
            });
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        avoidAutocomplete();
    }

    public void avoidAutocomplete()
    {
        AutoCompleteTextView text = (AutoCompleteTextView)findViewById(R.id.editCiudad);
        text.dismissDropDown();
    }
    

    public void botonListMore()
    {
    	eventListActivityRetained.getEventPageAdapter().continueEventSearch();
    }
    
    /*
     * Esta clase devuelve el control al hilo de la interfaz
     * Sirve para establecer el texto de la ciudad devuelta por el gps
     * en la caja de buscar y empezar la búsqueda
     */
    class ReturnFromLocation implements Runnable
    {
    	private Location location;
    	private String name;
    	private Context context;

    	public ReturnFromLocation(Context context, Location location, String name)
    	{
    		this.location = location;
    		this.name = name;
    		this.context = context;
    	}

		@Override
		public void run() 
		{
            AutoCompleteTextView text = (AutoCompleteTextView)findViewById(R.id.editCiudad);
			if (location!=null)
	    	{
				//Lanzamos la búsqueda para esta localizacion
    	    	Log.d(LOG_TAG,"Latitud y long devuelta...buscando eventos"+location);
                text.setFocusable(false);
                text.setFocusableInTouchMode(false);
				text.setText(name);
                text.setFocusable(true);
                text.setFocusableInTouchMode(true);
                text.dismissDropDown();
    	    	eventListActivityRetained.getEventPageAdapter().startEventSearch(location.getLatitude(), location.getLongitude());
    	    	//Deberíamos también poner el nombre de donde buscamos para informar al usuario de cual es la ubicación
	    	}
	    	else
	    	{
	    		DialogUtils.showMessageDialog(context, R.string.no_location_info_title, R.string.no_location_info_text);
                text.setText("");
	    	}	
		}
    	
    }


    
    
}