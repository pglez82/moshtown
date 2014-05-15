/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.lastfm.api.utils;

import de.umass.lastfm.ImageSize;

/**
 *
 * @author pablo
 */
public class LastFmImageSizeCalc
{
    /**
     * Enum de uso interna que relaciona los ImageSize con la anchura de la imagen
     * real
     */
    private enum LastFmImageSizeEnum
    {
        SMALL(ImageSize.SMALL,34),
        MEDIUM(ImageSize.MEDIUM,64),
        LARGE(ImageSize.LARGE,126),
        EXTRALARGE(ImageSize.EXTRALARGE,252),
        MEGA(ImageSize.MEGA,500);

        private ImageSize imageSize;
        private int width;
        
        private LastFmImageSizeEnum(ImageSize imageSize, int width)
        {
            this.imageSize = imageSize;
            this.width = width;
        }

        public int getWidth()
        {
            return width;
        }

        public ImageSize getImageSize()
        {
            return imageSize;
        }
    }
    
    public static ImageSize getOptimunImageSize(int imageWidth)
    {
        for (LastFmImageSizeEnum is : LastFmImageSizeEnum.values())
        {
            if (is.getWidth()>=imageWidth)
                return is.getImageSize();
        }
        
        return LastFmImageSizeEnum.MEGA.getImageSize();
    }
    
}
