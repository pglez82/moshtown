package es.concertsapp.android.gui.legal;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import es.concertsapp.android.gui.R;
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

        TextView textViewVersion = (TextView)findViewById(R.id.conditions_textview);
        FontUtils.setRobotoFont(this, textViewVersion, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
    }
}