package es.concertsapp.android.gui.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import es.concertsapp.android.conf.ConfValues;
import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.menu.MenuActivity;
import es.concertsapp.android.utils.DialogUtils;
import es.concertsapp.android.utils.font.FontUtils;

/**
 * Created by pablo on 13/05/14.
 */
public class SettingsActivity extends MenuActivity
{
    private final double MILLA_KM = 1.609344;
    private final int MIN_KM = 1;
    private final int MAX_KM = 500;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final TextView distanceTextView = (TextView) findViewById(R.id.distance_textview);
        final TextView settingsSeparatorTextView = (TextView)findViewById(R.id.settings_separator_textview);
        final SeekBar distanceSeekBar = (SeekBar) findViewById(R.id.distance_seekbar);
        final TextView distanceKmTextView = (TextView) findViewById(R.id.distance_km_text_view);
        final TextView distanceMiTextView = (TextView) findViewById(R.id.distance_milles_text_view);
        final Button saveButton = (Button) findViewById(R.id.settings_savebutton);

        FontUtils.setRobotoFont(this, distanceTextView, FontUtils.FontType.ROBOTOCONDENSED_BOLD);
        FontUtils.setRobotoFont(this, distanceKmTextView, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
        FontUtils.setRobotoFont(this, distanceMiTextView, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
        FontUtils.setRobotoFont(this, saveButton, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
        FontUtils.setRobotoFont(this, settingsSeparatorTextView, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);

        distanceSeekBar.setMax(MAX_KM-MIN_KM);
        distanceSeekBar.setProgress(ConfValues.getIntConfigurableValue(this, ConfValues.ConfigurableValue.EVENT_RATIO_DISTANCE)-MIN_KM);
        updateLabels(distanceSeekBar,distanceSeekBar.getProgress(),distanceKmTextView,distanceMiTextView);

        distanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updateLabels(seekBar,i,distanceKmTextView,distanceMiTextView);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfValues.setIntConfigurationValue(view.getContext(), ConfValues.ConfigurableValue.EVENT_RATIO_DISTANCE, distanceSeekBar.getProgress()+MIN_KM);
                DialogUtils.showToast(view.getContext(), Toast.LENGTH_LONG,R.string.save_ok_toast);
            }
        });
    }

    private void updateLabels(SeekBar seekBar, int i,TextView distanceKmTextView, TextView distanceMiTextView)
    {
        int km = i+MIN_KM;
        distanceKmTextView.setText(Integer.toString(km) + " " + getString(R.string.unit_km));
        distanceMiTextView.setText(Long.toString(Math.round(km / MILLA_KM)) + " " + getString(R.string.unit_mill));
    }
}
