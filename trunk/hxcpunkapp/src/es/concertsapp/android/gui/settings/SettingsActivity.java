package es.concertsapp.android.gui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.concertsapp.android.conf.ConfValues;
import es.concertsapp.android.gui.R;
import es.concertsapp.android.gui.menu.MenuActivity;
import es.concertsapp.android.utils.DialogUtils;
import es.concertsapp.android.utils.font.FontUtils;
import es.lastfm.api.connector.tags.PunkTagsExtended;

/**
 * Created by pablo on 13/05/14.
 */
public class SettingsActivity extends MenuActivity
{
    private final double MILLA_KM = 1.609344;
    private final int MIN_KM = 1;
    private final int MAX_KM = 500;
    private TagsAdapter tagsAdapter;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final TextView distanceTextView = (TextView) findViewById(R.id.distance_textview);
        final TextView settingsSeparatorTextView = (TextView)findViewById(R.id.settings_separator_textview);
        final SeekBar distanceSeekBar = (SeekBar) findViewById(R.id.distance_seekbar);
        final TextView distanceKmTextView = (TextView) findViewById(R.id.distance_km_text_view);
        final TextView distanceMiTextView = (TextView) findViewById(R.id.distance_milles_text_view);
        final TextView tagsTextView = (TextView)findViewById(R.id.tags_textview);
        final Button saveButton = (Button) findViewById(R.id.settings_savebutton);
        final Button defaultsButton = (Button) findViewById(R.id.settings_restoredefaults);

        FontUtils.setRobotoFont(this, distanceTextView, FontUtils.FontType.ROBOTOCONDENSED_BOLD);
        FontUtils.setRobotoFont(this, tagsTextView, FontUtils.FontType.ROBOTOCONDENSED_BOLD);
        FontUtils.setRobotoFont(this, distanceKmTextView, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
        FontUtils.setRobotoFont(this, distanceMiTextView, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
        FontUtils.setRobotoFont(this, saveButton, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
        FontUtils.setRobotoFont(this, defaultsButton, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
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
                Set<String> selectedTags = tagsAdapter.getSelectedTags();
                if (selectedTags.isEmpty())
                {
                    DialogUtils.showErrorDialog(view.getContext(),R.string.save_fail_toast);
                }
                else {
                    ConfValues.setIntConfigurationValue(view.getContext(), ConfValues.ConfigurableValue.EVENT_RATIO_DISTANCE, distanceSeekBar.getProgress()+MIN_KM);
                    SelectedTagsStore.getInstance().saveSelectedTags(tagsAdapter.getSelectedTags());
                    DialogUtils.showToast(view.getContext(), Toast.LENGTH_LONG, R.string.save_ok_toast);
                }
            }
        });

        defaultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                distanceSeekBar.setProgress(ConfValues.restoreIntConfigurationValue(view.getContext(), ConfValues.ConfigurableValue.EVENT_RATIO_DISTANCE)-MIN_KM);
                updateLabels(distanceSeekBar,distanceSeekBar.getProgress(),distanceKmTextView,distanceMiTextView);
                tagsAdapter.restoreDefaults();
                DialogUtils.showToast(view.getContext(), Toast.LENGTH_LONG,R.string.restore_ok_toast);
            }
        });

        //Parte de la lista de tags
        SelectedTagsStore selectedTagsStore = SelectedTagsStore.getInstance();
        ListView listViewTags = (ListView)findViewById(R.id.listtags);
        Set<String> selectedTags = selectedTagsStore.getSelectedTags();
        tagsAdapter = new TagsAdapter(selectedTagsStore.getAvailableTags(),selectedTags);
        listViewTags.setAdapter(tagsAdapter);

    }

    private void updateLabels(SeekBar seekBar, int i,TextView distanceKmTextView, TextView distanceMiTextView)
    {
        int km = i+MIN_KM;
        distanceKmTextView.setText(Integer.toString(km) + " " + getString(R.string.unit_km));
        distanceMiTextView.setText(Long.toString(Math.round(km / MILLA_KM)) + " " + getString(R.string.unit_mill));
    }

    private static class ViewHolder {
        CheckBox checkBox;
        TextView textView;
    }

    private class TagsAdapter extends BaseAdapter
    {
        private List<String> availableTags;
        private Set<String> selectedTags;

        public TagsAdapter(String[] availableTags, Set<String> selectedTags)
        {
            this.availableTags = Arrays.asList(availableTags);
            this.selectedTags = selectedTags;
        }

        @Override
        public int getCount() {
            return availableTags.size();
        }

        @Override
        public Object getItem(int i) {
            return availableTags.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View row = view;
            String tag = (String)this.getItem(i);

            ViewHolder viewHolder;
            if (row ==null)
            {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.list_tags_row, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView)row.findViewById(R.id.tagtextview);
                viewHolder.checkBox = (CheckBox)row.findViewById(R.id.tagcheckbox);

                viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        String tag = (String)compoundButton.getTag();
                        if (b)
                            selectedTags.add(tag);
                        else
                            selectedTags.remove(tag);
                    }
                });
                row.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder)row.getTag();
            }

            viewHolder.textView.setText(tag);
            //Almacenamos el valor para poder acceder desde el checkbox a la tag correspondiente
            viewHolder.checkBox.setTag(tag);
            viewHolder.checkBox.setChecked(selectedTags.contains(tag));
            FontUtils.setRobotoFont(getBaseContext(),viewHolder.textView, FontUtils.FontType.ROBOTOCONDENSED_LIGHT);
            return row;
        }

        public Set<String> getSelectedTags() {
            return selectedTags;
        }

        public void restoreDefaults()
        {
            selectedTags.clear();
            selectedTags.addAll(SelectedTagsStore.getInstance().restoreDefaultTags());
            notifyDataSetChanged();
        }
    }
}
