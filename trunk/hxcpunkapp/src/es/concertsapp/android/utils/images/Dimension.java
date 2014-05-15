package es.concertsapp.android.utils.images;

/**
 * Created by pablo on 22/10/13.
 */
public class Dimension
{
    private int fixedWidth;
    private int maxHeight;

    public Dimension(int width, int height)
    {
        this.fixedWidth = width;
        this.maxHeight = height;
    }

    public int getFixedWidth()
    {
        return fixedWidth;
    }

    public int getHeigh()
    {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight)
    {
        this.maxHeight = maxHeight;
    }

    public void setFixedWidth(int fixedWidth)
    {
        this.fixedWidth = fixedWidth;
    }
}
