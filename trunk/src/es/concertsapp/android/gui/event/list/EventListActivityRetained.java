package es.concertsapp.android.gui.event.list;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import es.concertsapp.android.gui.R;

/**
 * La filosofía de esta clase es evitar la volatilidad de las activitis cuando se produce un cambio
 * de orientación de la pantalla. Aquí almacenamos lo que no queremos que se nos vaya cuando se
 * cree otra vez la activity. La clave está en el método onAttach que nos vuelve a pasar la nueva
 * referencia de la activity para que nuestro hilo pueda seguir pasandole las cosas que va calculando
 * sin que casque.
 */
public class EventListActivityRetained extends Fragment
{
    public enum ListFooters
    {
        LOADING,
        LOAD_MORE,
        NO_RESULTS
    }

    private EventPageAdapter eventPageAdapter;
    private EventListActivity eventListActivity;

    //Indica que footer está activo. Hay que almacenarlo aquí porque esta clase es la que perdura
    //mientras que la activity desaparece
    private ListFooters activeFooter;

    public EventPageAdapter getEventPageAdapter()
    {
        return eventPageAdapter;
    }

    public EventListActivityRetained()
    {
        eventPageAdapter = new EventPageAdapter(eventListActivity, this, R.layout.item_row, R.layout.loading_row);
        activeFooter=null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * Hold a reference to the parent Activity so we can report the
     * task's current progress and results. The Android framework
     * will pass us a reference to the newly created Activity after
     * each configuration change.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        eventListActivity = (EventListActivity) activity;
        eventPageAdapter.setEventListActivity(eventListActivity);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        if (eventListActivity!=null)
            eventListActivity.updateFooters(activeFooter);
    }

    /**
     * Set the callback to null so we don't accidentally leak the
     * Activity instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        eventListActivity = null;
    }

    public void showFooter(ListFooters footer)
    {
        hideAllFooters();
        activeFooter=footer;
        if (eventListActivity!=null)
            eventListActivity.updateFooters(activeFooter);
    }

    public void hideAllFooters()
    {
        activeFooter=null;
        if (eventListActivity!=null)
            eventListActivity.updateFooters(activeFooter);
    }

}