package es.concertsapp.android.gui.band.list.favourites;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.umass.lastfm.ImageSize;
import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.band.detail.BandInfoActivity;
import es.concertsapp.android.utils.MyAppParameters;
import es.concertsapp.android.utils.images.ImageDownloader;
import es.lastfm.api.connector.dto.ArtistDTO;

/**
 * Created by pablo on 6/07/13.
 */
public class BandFavoritesFragment extends ListFragment
{
    private static final String LOG_TAG="BANDFAVORITESFRAGMENT";
    private FavouriteBandsStore favouriteBandsStore;
    private FavoriteBandsAdapter favouriteBandsAdapter;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        progressBar = (ProgressBar) getActivity().findViewById(R.id.progressbarfavourites);
        favouriteBandsStore = FavouriteBandsStore.getInstance(this);
        favouriteBandsStore.startNearEventBandSearch();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.band_favorites_list, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        favouriteBandsAdapter = new FavoriteBandsAdapter();
        //Cargamos la lista de bandas de disco
        getListView().setAdapter(favouriteBandsAdapter);
        favouriteBandsStore.addAdapterToNotify(favouriteBandsAdapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3)
            {
                Intent i = new Intent(getActivity(), BandInfoActivity.class);

                ArtistDTO artistDTO = favouriteBandsStore.getFavouriteBands().get(position);
                i.putExtra(MyAppParameters.BANDID, artistDTO.getArtistName());
                i.putExtra(MyAppParameters.FRAGMENTID, 1);
                startActivity(i);
            }
        });
    }

    public ProgressBar getProgressBar()
    {
        return progressBar;
    }

    static class BandSearchHolder
    {
        ImageView bandsearchImageView;
        TextView bandsearchName;
        Button favouriteButton;
    }

    private class FavoriteBandsAdapter extends BaseAdapter
    {

        private ImageDownloader imageDownloader;

        public FavoriteBandsAdapter()
        {
            imageDownloader=ImageDownloader.getInstance();
        }

        @Override
        public int getCount()
        {
            synchronized (favouriteBandsStore)
            {
                return favouriteBandsStore.getFavouriteBands().size();
            }
        }

        @Override
        public Object getItem(int position)
        {
            synchronized (favouriteBandsStore)
            {
                return favouriteBandsStore.getFavouriteBands().get(position);
            }
        }

        @Override
        public long getItemId(int position)
        {
            return position;
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
                holder.favouriteButton=(Button)row.findViewById(R.id.favouriteImageView);
                holder.favouriteButton.setBackgroundResource(R.drawable.nofavourite);


                row.setTag(holder);
            } else {
                holder = (BandSearchHolder) row.getTag();
            }

            holder.favouriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    favouriteBandsStore.removeFavouriteBand((ArtistDTO) getItem(position));
                }
            });

            ArtistDTO artistDTO = (ArtistDTO)this.getItem(position);
            holder.bandsearchName.setText(artistDTO.getArtistName());

            if (artistDTO.isNearEvents())
                holder.bandsearchName.setBackgroundColor(Color.RED);
            else
                holder.bandsearchName.setBackgroundColor(Color.TRANSPARENT);

            imageDownloader.download(artistDTO.getImageURL(ImageSize.MEDIUM), holder.bandsearchImageView);
            return row;
        }
    }
}
