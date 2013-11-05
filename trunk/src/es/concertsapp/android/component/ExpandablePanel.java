package es.concertsapp.android.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

import java.util.HashSet;
import java.util.Set;

import es.concertsapp.android.gui.R;

/**
 * Esta clase representa un panel collapsable. Tiene una referencia a el boton o lo que sea (que soporte onclick)
 * para colapsar y expander y una referencia al contenido (una view).
 * La gracia del asunto está en que cuando se va a redimensionar se calcula el espacio disponible y se expande
 * a tope. De esta manera coneseguimos adaptación máxima.
 */
public class ExpandablePanel extends LinearLayout
{

    private final int mHandleId;
    private final int mContentId;

    private View mHandle;
    private View mContent;

    private boolean mExpanded = false;
    private int mCollapsedHeight = 0;
    private int mAnimationDuration = 0;

    private Set<OnExpandListener> mListeners;

    public ExpandablePanel(Context context) {
        this(context, null);
    }

    public ExpandablePanel(Context context, AttributeSet attrs) {
        super(context, attrs);

        mListeners = new HashSet<OnExpandListener>();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandablePanel, 0, 0);

        // How high the content should be in "collapsed" state
        mCollapsedHeight = (int) a.getDimension(R.styleable.ExpandablePanel_collapsedHeight, 0.0f);

        // How long the animation should take
        mAnimationDuration = a.getInteger(R.styleable.ExpandablePanel_animationDuration, 500);

        int handleId = a.getResourceId(R.styleable.ExpandablePanel_handle, 0);
        if (handleId == 0) {
            throw new IllegalArgumentException(
                    "The handle attribute is required and must refer "
                            + "to a valid child.");
        }

        int contentId = a.getResourceId(R.styleable.ExpandablePanel_content, 0);
        if (contentId == 0) {
            throw new IllegalArgumentException("The content attribute is required and must refer to a valid child.");
        }

        mHandleId = handleId;
        mContentId = contentId;

        a.recycle();
    }

    public void addOnExpandListener(OnExpandListener listener) {
        mListeners.add(listener);
    }

    public void setCollapsedHeight(int collapsedHeight) {
        mCollapsedHeight = collapsedHeight;
    }

    public void setAnimationDuration(int animationDuration) {
        mAnimationDuration = animationDuration;
    }

    public boolean isExpanded()
    {
        return mExpanded;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mHandle = findViewById(mHandleId);
        mContent = findViewById(mContentId);
        if (mContent!=null && mHandle!=null)
        {
            android.view.ViewGroup.LayoutParams lp = mContent.getLayoutParams();
            lp.height = mCollapsedHeight;
            mContent.setLayoutParams(lp);

            mHandle.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    tooglePanel();
                }
            });
        }
    }

    private int spaceAvailable()
    {
        ExpandablePanelGroup container = (ExpandablePanelGroup)mContent.getParent().getParent();
        return container.getHeight()-(container.getPanelNumber()*mHandle.getHeight());
    }

    public void tooglePanel()
    {
        Animation a;
        int mContentHeight = spaceAvailable();
        if (mExpanded) {
            a = new ExpandAnimation(mContentHeight, mCollapsedHeight);
            a.setDuration(mAnimationDuration);
            mContent.startAnimation(a);
            for (OnExpandListener mListener : mListeners)
                mListener.onCollapse(getId(), mHandle, mContent);
        }
        else
        {
            a = new ExpandAnimation(mCollapsedHeight, mContentHeight);
            a.setDuration(mAnimationDuration);
            mContent.startAnimation(a);
            for (OnExpandListener mListener : mListeners)
                mListener.onExpand(getId(), mHandle, mContent);
        }
        mContent.invalidate();
        mHandle.invalidate();
        mExpanded = !mExpanded;
    }

    private class ExpandAnimation extends Animation {
        private final int mStartHeight;
        private final int mDeltaHeight;

        public ExpandAnimation(int startHeight, int endHeight) {
            mStartHeight = startHeight;
            mDeltaHeight = endHeight - startHeight;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            android.view.ViewGroup.LayoutParams lp = mContent.getLayoutParams();
            lp.height = (int) (mStartHeight + mDeltaHeight * interpolatedTime);
            mContent.setLayoutParams(lp);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }



    public interface OnExpandListener {
        public void onExpand(int id,View handle, View content);
        public void onCollapse(int id,View handle, View content);
    }
}