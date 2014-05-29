package es.concertsapp.android.gui.event.list;

import android.content.Context;
import android.text.Html;
import android.widget.TextView;

import java.util.Date;

import es.concertsapp.android.gui.R;
import es.concertsapp.android.utils.MyApplication;
import es.concertsapp.android.utils.date.DateFormater;
import es.concertsapp.android.utils.font.FontUtils;
import es.lastfm.api.connector.dto.ArtistEventDTO;
import es.lastfm.api.connector.dto.EventDTO;

/**
 * Created by pablo on 6/11/13.
 */
public class EventListHelper
{
    public static class EventHolder {
        public TextView eventDate;
        public TextView placeListInfo;
        public TextView concertListInfo;
    }

    private String letraGris;
    private String letraBlanco;

    private DateFormater dateFormater = DateFormater.getInstance(MyApplication.getLocale());

    public EventListHelper(Context context)
    {
        String grayHex = Integer.toHexString(context.getResources().getColor(R.color.gray3)).substring(2);
        String whiteHex = Integer.toHexString(context.getResources().getColor(R.color.white)).substring(2);
        letraGris = "<span><font color=\"#"+grayHex+"\">";
        letraBlanco = "<span><font color=\"#"+whiteHex+"\">";
    }

    private void loadInfoEvent(Context context, EventHolder holder, String eventPlace, String eventTitle, Date eventDate)
    {
        holder.concertListInfo.setText(eventTitle);
        FontUtils.setRobotoFont(context, holder.concertListInfo, FontUtils.FontType.ROBOTOCONDENSED_BOLD);
        holder.placeListInfo.setText(eventPlace);
        FontUtils.setRobotoFont(context,holder.placeListInfo, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
        String[] day = dateFormater.formatDay(eventDate);
        String[] month = dateFormater.formatMonth(eventDate);
        StringBuilder sb = new StringBuilder();
        sb.append(letraGris).append(day[0]).append("</span>").append(letraBlanco).append(day[1]).append("</span><br/>").append(letraBlanco).append(month[0]).append("</span>").append(letraGris).append(month[1]).append("</span>");
        holder.eventDate.setText(Html.fromHtml(sb.toString()));
        FontUtils.setRobotoFont(context, holder.eventDate, FontUtils.FontType.ROBOTOCONDENSED_BOLD);
    }

    public void loadInfoEvent(Context context, EventHolder holder, EventDTO eventDTO)
    {
        loadInfoEvent(context,holder,eventDTO.getEventPlace(), eventDTO.getEventTitle(), eventDTO.getEventDate());
    }

    public void loadInfoEvent(Context context, EventHolder holder, ArtistEventDTO artistEventDTO)
    {
        loadInfoEvent(context,holder,artistEventDTO.getEventPlace(), artistEventDTO.getEventTitle(), artistEventDTO.getStartDate());
    }
}
