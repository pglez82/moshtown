package es.concertsapp.android.gui.legal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.menu.MenuActivity;
import es.concertsapp.android.gui.settings.SettingsActivity;
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

        TextView textViewconditions = (TextView)findViewById(R.id.conditions_textview);
        FontUtils.setRobotoFont(this,textViewconditions, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
        TextView textViewSpotify = (TextView)findViewById(R.id.textViewSpotify);
        FontUtils.setRobotoFont(this, textViewSpotify, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
        textViewconditions.setMovementMethod(new ScrollingMovementMethod());
        textViewSpotify.setMovementMethod(new ScrollingMovementMethod());
        textViewSpotify.setMovementMethod(LinkMovementMethod.getInstance());
        ImageView imageLast = (ImageView)findViewById(R.id.im_powered_as);
        imageLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link=new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.last.fm"));
                startActivity(link);
            }
        });

        ImageButton imageButton3 = (ImageButton)findViewById(R.id.button_configuration);
        imageButton3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent myIntent = new Intent(LegalConditionsActivity.this, SettingsActivity.class);
                startActivity(myIntent);
            }
        });

    }
}