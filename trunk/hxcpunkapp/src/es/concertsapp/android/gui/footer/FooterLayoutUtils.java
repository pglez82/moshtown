package es.concertsapp.android.gui.footer;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.legal.LegalConditionsActivity;
import es.concertsapp.android.gui.legal.MoshTownConditionsActivity;
import es.concertsapp.android.gui.settings.SettingsActivity;

/**
 * Created by pablo on 17/09/14.
 */
public class FooterLayoutUtils
{
    /**
     * Este método para las activities
     * @param activity
     */
    public static void initializeButtonsFunctions(final Activity activity)
    {
        ImageButton lastfmButton = (ImageButton)activity.findViewById(R.id.button_logolastfm);
        ImageButton moshtownButton = (ImageButton)activity.findViewById(R.id.button_moshtown);
        ImageButton configurationButton = (ImageButton)activity.findViewById(R.id.button_configuration);
        setListeners(activity, moshtownButton,lastfmButton,configurationButton);
    }

    /**
     * Este método para los fragmentos
     * @param activity
     * @param rootView
     */
    public static void initializeButtonsFunctions(final Activity activity, final View rootView)
    {
        ImageButton lastfmButton = (ImageButton)rootView.findViewById(R.id.button_logolastfm);
        ImageButton moshtownButton = (ImageButton)rootView.findViewById(R.id.button_moshtown);
        ImageButton configurationButton = (ImageButton)rootView.findViewById(R.id.button_configuration);
        setListeners(activity, moshtownButton,lastfmButton,configurationButton);
    }

    private static void setListeners(final Activity activity, ImageButton moshtownButton, ImageButton lastFmButton, ImageButton configurationButton)
    {
        if (lastFmButton!=null) {
            lastFmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(activity, LegalConditionsActivity.class);
                    activity.startActivity(myIntent);
                }
            });
        }

        if (moshtownButton!=null) {
            moshtownButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(activity, MoshTownConditionsActivity.class);
                    activity.startActivity(myIntent);
                }
            });
        }

        if (configurationButton!=null)
        {
            configurationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent myIntent = new Intent(activity, SettingsActivity.class);
                activity.startActivity(myIntent);
                }
        });
        }
    }

}
