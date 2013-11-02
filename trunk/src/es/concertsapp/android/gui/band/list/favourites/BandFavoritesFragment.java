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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import es.concertsapp.android.component.LastFmImageView;
import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.band.detail.BandInfoActivity;
import es.concertsapp.android.utils.MyAppParameters;
import es.concertsapp.android.utils.font.FontUtils;
import es.lastfm.api.connector.dto.ArtistDTO;

public class BandFavoritesFragment extends ListFragment
{
    private FavouriteBandsStore favouriteBandsStore;
    private ProgressBar progressBar;
    private int progressBarState=View.INVISIBLE;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.band_favorites_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        FontUtils.setRobotoFont(this.getActivity(),getActivity().findViewById(R.id.mis_favoritos_title), FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
        progressBar = (ProgressBar) getActivity().findViewById(R.id.progressbarfavourites);
        progressBar.setVisibility(progressBarState);
        favouriteBandsStore = FavouriteBandsStore.getInstance(this);
        favouriteBandsStore.startNearEventBandSearch();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        FavoriteBandsAdapter favouriteBandsAdapter = new FavoriteBandsAdapter();
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

    public void setProgressBarState(int progressBarState)
    {
        if (progressBar!=null)
        {
            //noinspection MagicConstant
            progressBar.setVisibility(progressBarState);
            this.progressBarState = progressBarState;
        }
    }

    static class BandSearchHolder
    {
        LastFmImageView bandImageView;
        TextView favoriteName;
        ImageView favouriteButton;
    }

    private class FavoriteBandsAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return favouriteBandsStore.getFavouriteBands().size();
        }

        @Override
        public Object getItem(int position)
        {
            return favouriteBandsStore.getFavouriteBands().get(position);
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
            BandSearchHolder holder;

            if (row == null) {
                LayoutInflater inflater = (getActivity()).getLayoutInflater();
                row = inflater.inflate(R.layout.item_favourites_row, parent, false);
                holder = new BandSearchHolder();
                if (row!=null)
                {
                    holder.bandImageView =(LastFmImageView)row.findViewById(R.id.bandImageView);
                    holder.favoriteName = (TextView)row.findViewById(R.id.bandsearchName);
                    holder.favouriteButton=(ImageView)row.findViewById(R.id.favouriteImageView);
                    row.setTag(holder);
                }
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
            holder.favoriteName.setText(artistDTO.getArtistName());
            FontUtils.setRobotoFont(getActivity(), holder.favoriteName, FontUtils.FontType.ROBOTOCONDENSED_BOLD);

            if (artistDTO.isNearEvents())
                holder.favoriteName.setBackgroundColor(Color.RED);
            else
                holder.favoriteName.setBackgroundColor(Color.TRANSPARENT);

            //last.download(artistDTO.getImageURL(ImageSize.MEDIUM), holder.bandsearchImageView);
            holder.bandImageView.setLastFmImageSource(artistDTO);
            return row;
        }
    }
}
