package es.concertsapp.android.gui.event.detail;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.menu.MenuFragmentActivity;
import es.concertsapp.android.utils.MyAppParameters;

/**
 * Created by pablo on 31/08/13.
 */
public class EventMapActivity extends MenuFragmentActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_map);

        Bundle extras = getIntent().getExtras();
        double eventLat=extras.getDouble(MyAppParameters.EVENTLAT);
        double eventLon=extras.getDouble(MyAppParameters.EVENTLON);
        String eventPlaceName = extras.getString(MyAppParameters.EVENT_PLACE_NAME);

        LatLng pos = new LatLng(eventLat,eventLon);
        GoogleMap googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 14));
        googleMap.addMarker(new MarkerOptions()
                .position(pos)
                .title(eventPlaceName));
    }


}