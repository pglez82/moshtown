package es.concertsapp.android.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

/**
 * Created by pablo on 5/09/13.
 * Esta clase representa un grupo de paneles collapsables. Se encarga dejar expandido solo uno
 * de sus hijos.
 */
public class ExpandablePanelGroup extends LinearLayout
{
    public ExpandablePanelGroup(Context context)
    {
        this(context,null);
    }

    public ExpandablePanelGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        for (int i=0;i<this.getChildCount();i++)
        {
            ExpandablePanel expandablePanel = (ExpandablePanel)this.getChildAt(i);
            expandablePanel.addOnExpandListener(new ExpandablePanel.OnExpandListener()
            {
                @Override
                public void onExpand(int id, View handle, View content)
                {
                    closeAllOtherPanels(id);
                }

                @Override
                public void onCollapse(int id, View handle, View content)
                {

                }
            });
        }
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (getChildCount()>0)
                {
                    ((ExpandablePanel)getChildAt(0)).tooglePanel();
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }



    private void closeAllOtherPanels(int id)
    {
        for (int i=0;i<getChildCount();i++)
        {
            ExpandablePanel expandablePanel = (ExpandablePanel)getChildAt(i);
            if (expandablePanel.getId()!=id && expandablePanel.isExpanded())
                expandablePanel.tooglePanel();

        }
    }

    public int getPanelNumber()
    {
        return getChildCount();
    }


}
