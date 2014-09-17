package es.concertsapp.android.gui.legal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.settings.SettingsActivity;
import es.concertsapp.android.utils.font.FontUtils;

/**
 * Created by pablo on 22/11/13.
 */
public class MoshTownConditionsActivity extends Activity
{
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aviso_moshtown_layout);

        try
        {
            TextView textViewVersion = (TextView)findViewById(R.id.version);
            String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            FontUtils.setRobotoFont(this, textViewVersion, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
            textViewVersion.setText(version);
        }
        catch (Throwable e){}

        TextView textViewMoshTown = (TextView)findViewById(R.id.conditions_textview);
        FontUtils.setRobotoFont(this, textViewMoshTown, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);

        ImageButton imageButton3 = (ImageButton)findViewById(R.id.button_configuration);
        imageButton3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent myIntent = new Intent(MoshTownConditionsActivity.this, SettingsActivity.class);
                startActivity(myIntent);
            }
        });
    }
}