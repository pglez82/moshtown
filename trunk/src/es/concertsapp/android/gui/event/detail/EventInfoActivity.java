package es.concertsapp.android.gui.event.detail;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Collection;

import de.umass.lastfm.Event;
import de.umass.lastfm.ImageSize;
import es.concertsapp.android.component.ExpandablePanel;
import es.concertsapp.android.component.LastFmImageView;
import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.band.detail.BandInfoActivity;
import es.concertsapp.android.gui.legal.LegalConditionsActivity;
import es.concertsapp.android.gui.legal.MoshTownConditionsActivity;
import es.concertsapp.android.gui.menu.MenuFragmentActivity;
import es.concertsapp.android.utils.DialogUtils;
import es.concertsapp.android.utils.LastFmApiConnectorFactory;
import es.concertsapp.android.utils.MyAppParameters;
import es.concertsapp.android.utils.MyApplication;
import es.concertsapp.android.utils.UnexpectedErrorHandler;
import es.concertsapp.android.utils.date.DateFormater;
import es.concertsapp.android.utils.font.FontUtils;
import es.concertsapp.android.utils.images.ImageDownloader;
import es.lastfm.api.connector.LastFmApiConnector;
import es.lastfm.api.connector.dto.DetailedEventDTO;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

public class EventInfoActivity extends MenuFragmentActivity
{
	private static final String LOG_TAG = "EVENTINFOACTIVITY";
	private LastFmApiConnector lastFmApiConnector;
	private String[] bands;
	private DetailedEventDTO detailedEventDTO;
    private DateFormater dateFormater = DateFormater.getInstance(MyApplication.getLocale());

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
            TextView detailConcertName =  ((TextView)findViewById(R.id.detailedconcertname));
            detailConcertName.setText(detailedEventDTO.getTitle());
            //Bandas que tocan
            ListView listBandsView = (ListView) findViewById(R.id.listbandsevent);
            Collection<String> listBands = detailedEventDTO.getArtists();
            bands = new String[listBands.size()];
            listBands.toArray(bands);
            ArrayAdapter<String> arrayAdapter =  new ArrayAdapter<String>(this,R.layout.list_bands_row, bands);
            listBandsView.setAdapter(arrayAdapter);

            //Fecha del evento
            TextView textViewFechaEvent = (TextView)findViewById(R.id.fechaevent);
            textViewFechaEvent.setText(dateFormater.formatDateEvent(detailedEventDTO.getStartDate()));

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


            ExpandablePanel expandablePanelBio = (ExpandablePanel)findViewById(R.id.expandablepaneleventinfo);
            final ImageView flechaBio = (ImageView)findViewById(R.id.flechaeventinfo);
            expandablePanelBio.addOnExpandListener(new ExpandablePanel.OnExpandListener()
            {
                @Override
                public void onExpand(int id, View handle, View content)
                {
                    flechaBio.setImageResource(R.drawable.ic_flecha_arriba);
                }

                @Override
                public void onCollapse(int id, View handle, View content)
                {
                    flechaBio.setImageResource(R.drawable.ic_flecha_abajo);
                }
            });

            ExpandablePanel expandablePanelEventInfo = (ExpandablePanel)findViewById(R.id.expandablepaneleventinfo);
            expandablePanelEventInfo.addDefaultImageToggleListener((ImageView)findViewById(R.id.flechaeventinfo),R.drawable.ic_flecha_arriba, R.drawable.ic_flecha_abajo);
            ExpandablePanel expandablePanelEventBands = (ExpandablePanel)findViewById(R.id.expandablepaneleventbands);
            expandablePanelEventBands.addDefaultImageToggleListener((ImageView)findViewById(R.id.flechaeventbands),R.drawable.ic_flecha_arriba, R.drawable.ic_flecha_abajo);
            ExpandablePanel expandablePanelEventPoster = (ExpandablePanel)findViewById(R.id.expandablepaneleventposter);
            expandablePanelEventPoster.addDefaultImageToggleListener((ImageView)findViewById(R.id.flechaeventPOSTER),R.drawable.ic_flecha_arriba, R.drawable.ic_flecha_abajo);

            //Imagen del concierto
            ImageViewTouch eventImageView = (ImageViewTouch)findViewById(R.id.detailedEventImage);
            eventImageView.setDisplayType( ImageViewTouchBase.DisplayType.FIT_IF_BIGGER );
            ImageDownloader imageDownloader = ImageDownloader.getInstance();
            imageDownloader.download(detailedEventDTO.getImageURL(ImageSize.EXTRALARGE), eventImageView);


            TextView mapinfoevent = (TextView)findViewById(R.id.mapinfoevent);
            mapinfoevent.setText(detailedEventDTO.getEventPlace());

            TextView mapinfoeventaddress = (TextView)findViewById(R.id.mapinfoeventaddress);
            mapinfoeventaddress.setText(detailedEventDTO.getAddress());

            //Establecemos el listener para cuando nos pinchen en una banda del listado
            listBandsView.setOnItemClickListener(new OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
                {
                    Intent i = new Intent(EventInfoActivity.this, BandInfoActivity.class);
                    //TODO: Probablemente aqué haya que cambiar cosas para pasar un id
                    i.putExtra(MyAppParameters.BANDID, bands[position]);
                    startActivity(i);
                }
             });

            ImageButton imageButton = (ImageButton)findViewById(R.id.button_logolastfm);
            imageButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent myIntent = new Intent(EventInfoActivity.this, LegalConditionsActivity.class);
                    startActivity(myIntent);
                }
            });

            ImageButton moshtownButton = (ImageButton)findViewById(R.id.button_moshtown);
            moshtownButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent myIntent = new Intent(EventInfoActivity.this, MoshTownConditionsActivity.class);
                    startActivity(myIntent);
                }
            });


            FontUtils.setRobotoFont(this, findViewById(R.id.eventinfopanelbutton), FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
            FontUtils.setRobotoFont(this, findViewById(R.id.eventbandspanelbutton), FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
            FontUtils.setRobotoFont(this, findViewById(R.id.eventposterpanelbutton), FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
            FontUtils.setRobotoFont(this, detailConcertName, FontUtils.FontType.ROBOTOCONDENSED_BOLD);
            FontUtils.setRobotoFont(this, textViewFechaEvent, FontUtils.FontType.ROBOTOCONDENSED_BOLD);
            FontUtils.setRobotoFont(this, mapinfoevent, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
            FontUtils.setRobotoFont(this, mapinfoeventaddress, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
            FontUtils.setRobotoFont(this, linkTickets, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
            FontUtils.setRobotoFont(this, findViewById(R.id.listbandstext), FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
        }
        catch(Throwable e)
        {
            Log.e(LOG_TAG, "Se ha producido un error obteniendo la info del artista", e);
            //Si se produce un error aquí volvemos a la actividad anterior
            UnexpectedErrorHandler.handleUnexpectedError(this, e, new AlertDialog.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialogInterface)
                {
                    finish();
                }
            });
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
