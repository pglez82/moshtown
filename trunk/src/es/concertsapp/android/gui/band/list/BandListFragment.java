package es.concertsapp.android.gui.band.list;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.umass.lastfm.ImageSize;
import es.concertsapp.android.component.LastFmImageView;
import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.band.detail.BandInfoActivity;
import es.concertsapp.android.gui.band.list.favourites.FavouriteBandsStore;
import es.concertsapp.android.utils.LastFmApiConnectorFactory;
import es.concertsapp.android.utils.MyAppParameters;
import es.concertsapp.android.utils.UnexpectedErrorHandler;
import es.concertsapp.android.utils.font.FontUtils;
import es.concertsapp.android.utils.images.ImageDownloader;
import es.lastfm.api.connector.LastFmApiConnector;
import es.lastfm.api.connector.NewArtistAvaibleListener;
import es.lastfm.api.connector.dto.ArtistDTO;

public class BandListFragment extends ListFragment
{
    static final String LOG_TAG = "BANDLISTFRAGMENT";

	//Hilo para cargar las bandas
	private SearchBandsTask searchBandTask;

    private ProgressBar progressBar;

    private FavouriteBandsStore favouriteBandsStore;

    private Throwable errorBackground=null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        favouriteBandsStore=FavouriteBandsStore.getInstance(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)     
    {
        super.onCreate(savedInstanceState);    
        View rootView = inflater.inflate(R.layout.band_list, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (searchBandTask!=null)
        {
            ListView listView = getListView();
            searchBandTask.updateListView(listView);
            listView.setAdapter(searchBandTask.getSearchBandsAdapter());
        }
        progressBar=(ProgressBar)view.findViewById(R.id.progressbarbandlist);

        ImageButton buscarButon = (ImageButton)view.findViewById(R.id.buscarBandaButon);
        buscarButon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBands(v);
            }
        });

        final EditText searchTextView = (EditText)view.findViewById(R.id.editBanda);
        FontUtils.setRobotoFont(getActivity(),searchTextView, FontUtils.FontType.ROBOTO_LIGHT);
        searchTextView.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    if (keyCode==KeyEvent.KEYCODE_ENTER)
                    {
                        searchBands(getView());
                        return true;
                    }
                }
                return false;
            }
        });

        //Listener del botón de borrar
        ImageButton imageButton = (ImageButton)view.findViewById(R.id.buttonClear);
        imageButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                searchTextView.setText("");
            }
        });

    }

	/*
	 * Función que se ejecuta cuando se pulsa el botón buscar
	 */
	private void searchBands(View v)
	{
		EditText text = (EditText)this.getActivity().findViewById(R.id.editBanda);
		String bandName = text.getText().toString();
		if (bandName!=null && !"".equals(bandName))
		{
			//Si ya hay una búsqueda la cancelamos
			if (searchBandTask!=null)
				searchBandTask.cancel(true);
			
			searchBandTask=new SearchBandsTask(getListView());
			searchBandTask.execute(bandName);
		}
	}

	static class BandSearchHolder
	{
		LastFmImageView bandsearchImageView;
		TextView bandsearchName;
        ImageView favouriteButton;
	}
	
	public class SearchBandsTask extends AsyncTask<String, ArtistDTO, Void> implements NewArtistAvaibleListener
	{	
		//Donde se van a cargar los datos
		private ListView listView;
		private List<ArtistDTO> listBands;
		private SearchBandsAdapter searchBandsAdapter;



        //Adapter para mostrar los datos cargados por este hilo
		public class SearchBandsAdapter extends BaseAdapter
		{	
			
			private ImageDownloader imageDownloader;
			
			public SearchBandsAdapter()
			{
				imageDownloader=ImageDownloader.getInstance();
			}

			@Override
			public int getCount() 
			{
				synchronized (listBands) 
		    	{
					return listBands.size();
				}
			}

			@Override
			public Object getItem(int position) 
			{
				synchronized (listBands) 
		    	{
		    		return listBands.get(position);
				}	
			}

			@Override
			public long getItemId(int position) 
			{
				return position;
			}

            private void setAddFavouriteButton(final ImageView button, final int position)
            {
                button.setBackgroundResource(R.drawable.button_estrella_off_image);
                button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        favouriteBandsStore.addFavouriteBand((ArtistDTO) getItem(position));
                        setRemoveFavouriteButton(button,position);
                    }
                });

            }

            private void setRemoveFavouriteButton(final ImageView button,final int position)
            {
                button.setBackgroundResource(R.drawable.button_estrella_on_image);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        favouriteBandsStore.removeFavouriteBand((ArtistDTO) getItem(position));
                        setAddFavouriteButton(button,position);
                    }
                });
            }

			@Override
			public View getView(final int position, View convertView, ViewGroup parent)
			{
				View row = convertView;
				BandSearchHolder holder = null;

				if (row == null) {
					LayoutInflater inflater = (getActivity()).getLayoutInflater();
					row = inflater.inflate(R.layout.item_searchband_row, parent, false);

					holder = new BandSearchHolder();
					holder.bandsearchImageView =(LastFmImageView)row.findViewById(R.id.bandsearchImageView);
					holder.bandsearchName = (TextView)row.findViewById(R.id.bandsearchName);
                    holder.favouriteButton=(ImageView)row.findViewById(R.id.favouriteImageView);
					row.setTag(holder);
				} else {
					holder = (BandSearchHolder) row.getTag();
				}

                //Este trozo de codigo se tiene que ejecutar siempre porque los datos se pueden actualizar
                //en los dos fragmentos y así se sincroniza la cosa bien

                if (favouriteBandsStore.getFavouriteBands().contains(getItem(position)))
                {
                    setRemoveFavouriteButton(holder.favouriteButton,position);
                }
                else
                {
                    setAddFavouriteButton(holder.favouriteButton,position);
                }

				ArtistDTO artistDTO = (ArtistDTO)this.getItem(position);
                FontUtils.setRobotoFont(getActivity(),holder.bandsearchName, FontUtils.FontType.ROBOTO_BOLD);
				holder.bandsearchName.setText(artistDTO.getArtistName());
				//imageDownloader.download(artistDTO.getImageURL(ImageSize.MEDIUM), holder.bandsearchImageView);
                holder.bandsearchImageView.setLastFmImageSource(artistDTO);
				return row;
			}
		}
		
		public SearchBandsTask(ListView listView)
		{
			this.listView = listView;
		}

        public void updateListView(ListView listView)
        {
            this.listView=listView;
            listView.setAdapter(this.searchBandsAdapter);
            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {
                    Intent i = new Intent(getActivity(),BandInfoActivity.class);
                    ArtistDTO artistDTO = listBands.get(position);
                    i.putExtra(MyAppParameters.BANDID, artistDTO.getArtistName());
                    startActivity(i);
                }
            });
        }

        public void notifyNewFavorites()
        {
            searchBandsAdapter.notifyDataSetChanged();
        }

        public SearchBandsAdapter getSearchBandsAdapter()
        {
            return searchBandsAdapter;
        }

		@Override
		protected Void doInBackground(String... params) 
		{
			LastFmApiConnector lastFmApiConnector = LastFmApiConnectorFactory.getInstance();
            try
            {
			    lastFmApiConnector.getArtists(params[0],this);
            }
            catch (Throwable e)
            {
                errorBackground = e;
            }
			return null;
		}

		
		
		

		@Override
		protected void onProgressUpdate(ArtistDTO... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			if (!isCancelled())
			{
		         this.listBands.add(values[0]);
		         this.searchBandsAdapter.notifyDataSetChanged();
			}
			
		}

		@Override
		protected void onPreExecute()
        {
			super.onPreExecute();
            errorBackground=null;
			this.listBands=new ArrayList<ArtistDTO>();

            progressBar.setVisibility(View.VISIBLE);
			this.searchBandsAdapter = new SearchBandsAdapter();
            favouriteBandsStore.addAdapterToNotify(searchBandsAdapter);
            updateListView(listView);
            notifyNewFavorites();
		}
		
		

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
            progressBar.setVisibility(View.INVISIBLE);
            if (errorBackground!=null)
                UnexpectedErrorHandler.handleUnexpectedError(getActivity(), errorBackground);
            if (listBands==null || listBands.isEmpty())
            {
                //TODO: Sacar un mensaje de que no hay resultados
            }
		}

		@Override
		public void newArtistAvailable(ArtistDTO artist) {
			this.publishProgress(artist);
		}
	}
	

}
