package es.concertsapp.android.gui.legal;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.menu.MenuActivity;
import es.concertsapp.android.utils.font.FontUtils;

/**
 * Created by pablo on 2/11/13.
 */
public class LegalConditionsActivity extends MenuActivity
{
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aviso_legal_layout);

        TextView textViewVersion = (TextView)findViewById(R.id.conditions_textview);
        FontUtils.setRobotoFont(this,textViewVersion, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
        TextView textViewSpotify = (TextView)findViewById(R.id.textViewSpotify);
        FontUtils.setRobotoFont(this,textViewSpotify, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
        textViewVersion.setMovementMethod(new ScrollingMovementMethod());
        textViewSpotify.setMovementMethod(new ScrollingMovementMethod());
        textViewVersion.setMovementMethod(LinkMovementMethod.getInstance());
        textViewSpotify.setMovementMethod(LinkMovementMethod.getInstance());

    }
}