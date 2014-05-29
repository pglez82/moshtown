package es.concertsapp.android.utils.rateshare;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import es.concertsapp.android.gui.R;
import es.concertsapp.android.utils.DialogUtils;
import es.lastfm.api.connector.dto.DetailedEventDTO;

/**
 * Created by pablo on 23/12/13.
 */
public class RateShareActions
{
    private static final String appId = "es.concertsapp.android.gui";

    private boolean startActivity(Context context, Intent aIntent) {
        try
        {
            context.startActivity(aIntent);
            return true;
        }
        catch (ActivityNotFoundException e)
        {
            return false;
        }
    }

    public void rateApp(Context context)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id="+appId));
        if (!startActivity(context, intent)) {
            //Market (Google play) app seems not installed, let's try to open a webbrowser
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + appId));
            if (!startActivity(context, intent)) {
                //Well if this also fails, we have run out of options, inform the user.
                DialogUtils.showToast(context, Toast.LENGTH_LONG, R.string.install_market);
            }
        }
    }

    public void shareApp(Context context)
    {
        if (context!=null)
        {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, context.getString(R.string.share_title));
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, context.getString(R.string.share_text)+" https://play.google.com/store/apps/details?id="+appId);
            context.startActivity(Intent.createChooser(sharingIntent, context.getString(R.string.share_via)));
        }
    }

    public void shareEvents(Context context, DetailedEventDTO event)
    {
        if (context!=null)
        {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, context.getString(R.string.share_title));
            sharingIntent.putExtra(Intent.EXTRA_TEXT,context.getString(R.string.share_event_text)+" "+event.getTitle() +" - "+event.getEventPlace()+" http://www.moshtown.com/events/" + event.getId());
            context.startActivity(Intent.createChooser(sharingIntent, context.getString(R.string.share_via)));
        }
    }
}
