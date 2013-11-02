package es.concertsapp.android.gui.event.list;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import es.concertsapp.android.utils.font.FontUtils;
import es.concertsapp.android.utils.geo.PlaceInterface;
import es.concertsapp.android.utils.geo.impl.GooglePlacesApi;

/**
 * Created by pablo on 9/08/13.
 */
public class PlacesAutoCompleteAdapter extends ArrayAdapter<PlaceInterface> implements Filterable
{
    private List<PlaceInterface> listPlaces;
    private GooglePlacesApi googlePlacesApi = new GooglePlacesApi();
    private int normalResultView;
    private List<PlaceInterface> listPlacesSearched;

    private int dropDownFontColorVisited = Color.parseColor("#808080");

    public PlacesAutoCompleteAdapter(Context context, int normalResultView,List<PlaceInterface> listPlacesSearched) {
        super(context, normalResultView);
        this.normalResultView = normalResultView;
        this.listPlacesSearched = listPlacesSearched;
    }

    @Override
    public int getCount() {
        return listPlaces.size();
    }

    @Override
    public PlaceInterface getItem(int index) {
        return listPlaces.get(index);
    }

    @Override
    public Filter getFilter()
    {
        Filter filter = new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                FilterResults filterResults = new FilterResults();
                if (constraint != null)
                {
                    listPlaces = googlePlacesApi.autocomplete(constraint.toString(),null);
                    Collections.sort(listPlaces, new Comparator<PlaceInterface>()
                    {
                        @Override
                        public int compare(PlaceInterface placeInterface, PlaceInterface placeInterface2)
                        {
                            if (listPlacesSearched.contains(placeInterface) && !listPlacesSearched.contains(placeInterface2))
                                return -1;
                            else if (listPlacesSearched.contains(placeInterface2) && !listPlacesSearched.contains(placeInterface))
                                return 1;
                            else return placeInterface.getPlaceName().compareTo(placeInterface2.getPlaceName());
                        }
                    });
                    if (listPlaces !=null)
                    {
                        filterResults.values = listPlaces;
                        filterResults.count = listPlaces.size();
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results)
            {
                if (results != null && results.count > 0)
                {
                    notifyDataSetChanged();
                }
                else
                {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(normalResultView, parent, false);
        }


        //Mostramos el elemento
        PlaceInterface place = this.getItem(position);
        TextView textView = (TextView)convertView;
        textView.setText(place.getPlaceName());

        if (listPlacesSearched.contains(place))
        {
            FontUtils.setRobotoFont(this.getContext(),textView, FontUtils.FontType.ROBOTOCONDENSED_BOLD);
            textView.setTextColor(dropDownFontColorVisited);
        }
        else
            FontUtils.setRobotoFont(this.getContext(),textView, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);

        return convertView;
    }

    public void setListPlacesSearched(List<PlaceInterface> listPlacesSearched)
    {
        this.listPlacesSearched = listPlacesSearched;
    }
}

