package es.concertsapp.android.gui.event.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Collection;

import de.umass.lastfm.Event;
import de.umass.lastfm.ImageSize;
import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.band.detail.BandInfoActivity;
import es.concertsapp.android.gui.menu.MenuFragmentActivity;
import es.concertsapp.android.utils.DialogUtils;
import es.concertsapp.android.utils.LastFmApiConnectorFactory;
import es.concertsapp.android.utils.MyAppParameters;
import es.concertsapp.android.utils.UnexpectedErrorHandler;
import es.concertsapp.android.utils.images.ImageDownloader;
import es.lastfm.api.connector.LastFmApiConnector;
import es.lastfm.api.connector.dto.DetailedEventDTO;
import es.lastfm.api.connector.exception.LastFmException;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

public class EventInfoActivity extends MenuFragmentActivity
{
	private static final String LOG_TAG = "EVENTINFOACTIVITY";
	private LastFmApiConnector lastFmApiConnector;
	private String[] bands;
	private DetailedEventDTO detailedEventDTO;

    @Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_concert_info);
		
		//Recogemos el parámetro que nos viene del listado
		Bundle extras = getIntent().getExtras();
		int eventid=extras.getInt(MyAppParameters.EVENTID);
		
		//Conectamos con last.fm para obtener la información detallada del evento
		lastFmApiConnector=LastFmApiConnectorFactory.getInstance();
        try
        {
            detailedEventDTO=lastFmApiConnector.getDetailedInfoEvent(eventid);
            ((TextView)findViewById(R.id.detailedconcertname)).setText(detailedEventDTO.getTitle());
            //Bandas que tocan
            ListView listBandsView = (ListView) findViewById(R.id.detailedlistbands);
            View header = getLayoutInflater().inflate(R.layout.list_bands_header, null);
            listBandsView.addHeaderView(header,null,false);
            Collection<String> listBands = detailedEventDTO.getArtists();
            bands = new String[listBands.size()];
            listBands.toArray(bands);
            ArrayAdapter<String> arrayAdapter =  new ArrayAdapter<String>(this,R.layout.list_bands_row, bands);
            listBandsView.setAdapter(arrayAdapter);

            //Información sobre los tickets
            String linkText="";
            TextView linkTickets = (TextView)findViewById(R.id.detailedconcerttickes);
            if (detailedEventDTO.getTicketsInfo()!=null && detailedEventDTO.getTicketsInfo().size()>0)
            {
                for (Event.TicketSupplier ticketSupplier : detailedEventDTO.getTicketsInfo())
                {
                    linkText += "<a href='"+ticketSupplier.getWebsite()+"'>"+ticketSupplier.getName()+"</a>";
                }

                linkTickets.setText(Html.fromHtml(linkText));
                linkTickets.setMovementMethod(LinkMovementMethod.getInstance());
            }
            else
            {
                //NO hay tickets
                linkTickets.setText(R.string.no_tickets_available);
            }

            //Imagen del concierto
            ImageViewTouch eventImageView = (ImageViewTouch)findViewById(R.id.detailedEventImage);
            eventImageView.setDisplayType( ImageViewTouchBase.DisplayType.FIT_IF_BIGGER );
            ImageDownloader imageDownloader = ImageDownloader.getInstance();
            imageDownloader.download(detailedEventDTO.getImageURL(ImageSize.LARGE), eventImageView);

            TextView descTextView = (TextView)findViewById(R.id.detailedconcertdesc);
            descTextView.setMovementMethod(new ScrollingMovementMethod());
            descTextView.setText(Html.fromHtml(detailedEventDTO.getDescription()));

            //Establecemos el listener para cuando nos pinchen en una banda del listado
            listBandsView.setOnItemClickListener(new OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
                {
                    if (position>0)
                    {
                        Intent i = new Intent(EventInfoActivity.this, BandInfoActivity.class);
                        //TODO: Probablemente aqué haya que cambiar cosas para pasar un id
                        i.putExtra(MyAppParameters.BANDID, bands[position-1]);
                        startActivity(i);
                    }
                    else if (position==0)
                    {
                        //nos pulsaron en la cabera
                    }

                }
        });
        }
        catch(LastFmException e)
        {
            Log.e(LOG_TAG,"Se ha producido un error obteniendo la info del artista",e);
            UnexpectedErrorHandler.handleUnexpectedError(e);
            DialogUtils.showErrorDialog(this,R.string.lastfm_error);
        }
	}

	public void buttonAddCalendar(View view)
	{
		Log.d(LOG_TAG, "Añadiendo el evento al calendario");  
		Calendar calendarStart = Calendar.getInstance();
		calendarStart.setTime(detailedEventDTO.getStartDate());
		Calendar calendarEnd = Calendar.getInstance();
		if (detailedEventDTO.getEndDate()!=null)
			calendarEnd.setTime(detailedEventDTO.getEndDate());
		else
			calendarEnd.setTimeInMillis(calendarStart.getTimeInMillis()+(3600*1000));
		Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra("beginTime",calendarStart.getTimeInMillis());
		intent.putExtra("allDay", false);
		intent.putExtra("endTime", calendarEnd.getTimeInMillis());
		intent.putExtra("title", detailedEventDTO.getTitle());
		intent.putExtra("eventLocation", detailedEventDTO.getEventPlace());
		intent.putExtra("description", detailedEventDTO.getDescription()!=null ? Html.fromHtml(detailedEventDTO.getDescription()).toString() : "");
		try
		{
			startActivity(intent);
		}
		catch (Throwable e)
		{
			DialogUtils.showMessageDialog(this, R.string.no_calendar_title, R.string.no_calendar_message);
		}
	}

    public void buttonShowMap(View view)
    {
        Intent i = new Intent(this, EventMapActivity.class);
        i.putExtra(MyAppParameters.EVENTLAT, detailedEventDTO.getLatEventPlace());
        i.putExtra(MyAppParameters.EVENTLON, detailedEventDTO.getLonEventPlace());
        i.putExtra(MyAppParameters.EVENT_PLACE_NAME, detailedEventDTO.getEventPlace());
        startActivity(i);
    }

}
