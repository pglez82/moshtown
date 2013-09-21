package es.concertsapp.android.gui.band.list;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.umass.lastfm.ImageSize;
import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.band.detail.BandInfoActivity;
import es.concertsapp.android.gui.band.list.favourites.FavouriteBandsStore;
import es.concertsapp.android.utils.DialogUtils;
import es.concertsapp.android.utils.LastFmApiConnectorFactory;
import es.concertsapp.android.utils.MyAppParameters;
import es.concertsapp.android.utils.images.ImageDownloader;
import es.lastfm.api.connector.LastFmApiConnector;
import es.lastfm.api.connector.NewArtistAvaibleListener;
import es.lastfm.api.connector.dto.ArtistDTO;
import es.lastfm.api.connector.exception.LastFmException;

public class BandListFragment extends ListFragment
{
    static final String LOG_TAG = "BANDLISTFRAGMENT";
    public enum ListFooters
    {
        LOADING,
        NO_RESULTS
    }

	//Hilo para cargar las bandas
	private SearchBandsTask searchBandTask;
	//Elemento cargando
	private View loadingFooter;
    private View noResultsFooter;
    private ListFooters activeFooter;

    private FavouriteBandsStore favouriteBandsStore;

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
        loadingFooter = inflater.inflate(R.layout.loading_row, null);
        noResultsFooter = inflater.inflate(R.layout.no_results_footer,null);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (searchBandTask!=null)
        {
            searchBandTask.setListView(getListView());
            getListView().addFooterView(loadingFooter);
            getListView().addFooterView(noResultsFooter);
            getListView().setAdapter(searchBandTask.getSearchBandsAdapter());
            getListView().removeFooterView(loadingFooter);
            getListView().removeFooterView(noResultsFooter);
            showFooter(activeFooter);
        }

    }

    @Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
		//Asociamos el botón con su evento
		
		Button buscarButon = (Button)this.getActivity().findViewById(R.id.buscarBandaButon);
        buscarButon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchBands(v);
			}
		});

        EditText searchTextView = (EditText)this.getActivity().findViewById(R.id.editBanda);
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

    public void showFooter(ListFooters footer)
    {
        hideAllFooters();
        activeFooter=footer;
        if (activeFooter==ListFooters.LOADING)
        {
            Log.d(LOG_TAG, "Activando el footer loading");
            getListView().addFooterView(loadingFooter,null,false);
            loadingFooter.setVisibility(View.VISIBLE);
        }
        else if (activeFooter==ListFooters.NO_RESULTS)
        {
            Log.d(LOG_TAG, "Activando el footer no results");
            getListView().addFooterView(noResultsFooter,null,false);
            noResultsFooter.setVisibility(View.VISIBLE);
        }
    }

    public void hideAllFooters()
    {
        Log.d(LOG_TAG, "Ocultando todos los footers. El footer activo es: " + activeFooter);
        ListView listView = getListView();
        if (activeFooter==ListFooters.LOADING)
        {
            listView.removeFooterView(loadingFooter);
            loadingFooter.setVisibility(View.GONE);
        }
        if (activeFooter==ListFooters.NO_RESULTS)
        {
            listView.removeFooterView(noResultsFooter);
            noResultsFooter.setVisibility(View.GONE);
        }
        activeFooter=null;
    }
	
	static class BandSearchHolder 
	{
		ImageView bandsearchImageView;
		TextView bandsearchName;
        Button favouriteButton;
	}
	
	public class SearchBandsTask extends AsyncTask<String, ArtistDTO, Void> implements NewArtistAvaibleListener
	{	
		//Donde se van a cargar los datos
		private ListView listView;
		private List<ArtistDTO> listBands;
		private SearchBandsAdapter searchBandsAdapter;

        public void notifyNewFavorites()
        {
            searchBandsAdapter.notifyDataSetChanged();
        }

        public SearchBandsAdapter getSearchBandsAdapter()
        {
            return searchBandsAdapter;
        }

        public void setListView(ListView listView)
        {
            this.listView = listView;
        }

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

            private void setAddFavouriteButton(final Button button, final int position)
            {
                button.setBackgroundResource(R.drawable.favourite);
                button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        favouriteBandsStore.addFavouriteBand((ArtistDTO) getItem(position));
                        setRemoveFavouriteButton(button,position);
                    }
                });

            }

            private void setRemoveFavouriteButton(final Button button,final int position)
            {
                button.setBackgroundResource(R.drawable.nofavourite);
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
					holder.bandsearchImageView =(ImageView)row.findViewById(R.id.bandsearchImageView);
					holder.bandsearchName = (TextView)row.findViewById(R.id.bandsearchName);


					row.setTag(holder);
				} else {
					holder = (BandSearchHolder) row.getTag();
				}

                //Este trozo de codigo se tiene que ejecutar siempre porque los datos se pueden actualizar
                //en los dos fragmentos y así se sincroniza la cosa bien
                holder.favouriteButton=(Button)row.findViewById(R.id.favouriteImageView);
                if (favouriteBandsStore.getFavouriteBands().contains(getItem(position)))
                {
                    setRemoveFavouriteButton(holder.favouriteButton,position);
                }
                else
                {
                    setAddFavouriteButton(holder.favouriteButton,position);
                }

				ArtistDTO artistDTO = (ArtistDTO)this.getItem(position);
				holder.bandsearchName.setText(artistDTO.getArtistName());
				imageDownloader.download(artistDTO.getImageURL(ImageSize.MEDIUM), holder.bandsearchImageView);
				return row;
			}
		}
		
		public SearchBandsTask(ListView listView)
		{
			this.listView = listView;
		}
		
		@Override
		protected Void doInBackground(String... params) 
		{
			LastFmApiConnector lastFmApiConnector = LastFmApiConnectorFactory.getInstance();
            try
            {
			    lastFmApiConnector.getArtists(params[0],this);
            }
            catch (LastFmException e)
            {
                DialogUtils.showErrorDialog(getActivity(),R.string.lastfm_error);
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
			this.listBands=new ArrayList<ArtistDTO>();

            showFooter(ListFooters.LOADING);
			this.searchBandsAdapter = new SearchBandsAdapter();
			listView.setAdapter(this.searchBandsAdapter);
            favouriteBandsStore.addAdapterToNotify(searchBandsAdapter);
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
            notifyNewFavorites();
		}
		
		

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
            if (listBands==null || listBands.isEmpty())
                showFooter(ListFooters.NO_RESULTS);
            else
                hideAllFooters();
		}

		@Override
		public void newArtistAvailable(ArtistDTO artist) {
			this.publishProgress(artist);
		}
	}
	

}
