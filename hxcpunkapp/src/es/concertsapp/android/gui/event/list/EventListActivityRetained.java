package es.concertsapp.android.gui.event.list;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ProgressBar;

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
    public enum ListElementsOnlyOneVisible
    {
        NO_RESULTS,
        LOADING,
        MORE_RESULTS
    }

    private EventPageAdapter eventPageAdapter;
    private EventListActivity eventListActivity;

    //Solo hay un elemento visible al mismo tiempo
    private ListElementsOnlyOneVisible visibleElement;

    private ProgressBar progressBar;
    private View loadMoreView;
    private View emptyView;

    public EventPageAdapter getEventPageAdapter()
    {
        return eventPageAdapter;
    }

    public EventListActivityRetained()
    {
    }

    public EventListActivityRetained(EventListActivity eventListActivity)
    {
        this.eventListActivity = eventListActivity;
        eventPageAdapter = new EventPageAdapter(eventListActivity, this, R.layout.item_row);
        visibleElement=null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setProgressBar(ProgressBar progressBar)
    {
        this.progressBar = progressBar;
    }

    public void setLoadMoreView(View loadMoreView)
    {
        this.loadMoreView = loadMoreView;
    }

    public void setEmptyView(View emptyView)
    {
        this.emptyView  = emptyView;
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
        if (eventPageAdapter!=null)
            eventPageAdapter.setEventListActivity(eventListActivity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        showElement(visibleElement);
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

    public void showElement(ListElementsOnlyOneVisible element)
    {
        hideAllElements();
        visibleElement=element;
        if (visibleElement!=null)
        {
            switch (visibleElement)
            {
                case LOADING:
                    if (progressBar!=null) progressBar.setVisibility(View.VISIBLE);
                    break;
                case MORE_RESULTS:
                    if (loadMoreView!=null) loadMoreView.setVisibility(View.VISIBLE);
                    break;
                case NO_RESULTS:
                    if (eventListActivity!=null && eventListActivity.getListView()!=null) eventListActivity.getListView().setVisibility(View.GONE);
                    if (emptyView!=null) emptyView.setVisibility(View.VISIBLE);
                    break;

            }
        }
    }

    public void hideAllElements()
    {
        visibleElement=null;
        if (emptyView!=null) emptyView.setVisibility(View.GONE);
        if (eventListActivity!=null && eventListActivity.getListView()!=null) eventListActivity.getListView().setVisibility(View.VISIBLE);
        if (progressBar!=null) progressBar.setVisibility(View.INVISIBLE);
        if (loadMoreView!=null) loadMoreView.setVisibility(View.INVISIBLE);
    }

}
