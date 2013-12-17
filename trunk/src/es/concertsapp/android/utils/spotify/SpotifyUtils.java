package es.concertsapp.android.utils.spotify;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

/**
 * Created by pablo on 28/11/13.
 */
public class SpotifyUtils
{
    private static SpotifyUtils singleton = null;

    private static final String spotifyUri = "com.spotify.mobile.android.ui";

    private SpotifyUtils()
    {

    }

    public static SpotifyUtils getInstance()
    {
        if (singleton==null)
            singleton = new SpotifyUtils();
        return singleton;
    }

    /**
     * Esta función chequea si spotify está instalado o no
     * @return true si lo está, falso si no lo está
     */
    public boolean isSpotifyInstalled(Context context)
    {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try
        {
            pm.getPackageInfo(spotifyUri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            app_installed = false;
        }
        return app_installed ;
    }

    public void lauchSpotifyInstall(Context context)
    {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + spotifyUri)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + spotifyUri)));
        }
    }

    public void launchSpotifyArtist(Context context, String spotifyLink)
    {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(spotifyLink)));
    }
}
