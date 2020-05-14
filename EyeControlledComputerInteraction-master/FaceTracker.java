 

import java.awt.*;
import java.awt.image.*;
import org.generation5.vision.LineHoughTransformOp;

 

public class FaceTracker
{

  final byte red = (byte)Color.red.getRed();
  final byte green = (byte)Color.red.getGreen();
  final byte blue = (byte)Color.red.getBlue();
  final byte red2 = (byte)Color.black.getRed();
  final byte green2 = (byte)Color.black.getGreen();
  final byte blue2 = (byte)Color.black.getBlue();
  int tMotionPixels[];
  LineHoughTransformOp hough;
  int slx,sly,srx,sry;

  public FaceTracker(int pWid,int pHei)
  {
    tMotionPixels = new int[pWid*pHei];
    hough = new LineHoughTransformOp();
    hough.setLineColor(Color.cyan);
    hough.setLocalPeakNeighbourhood(7);
  }

  private double calculateSSD(int template[][],int ROI[]
                              ,int tWid,int sWid,int x0,int y0,double min,int tHei)
  {
    double error = 0;
    for (int y = 0; y < tHei / 4; y++)
      for (int x = 0; x < tWid; x++)
        error += Math.pow(template[x][y] - ROI[ (y + y0) * sWid + (x + x0)], 2);

    if (error > min)
      return Double.MAX_VALUE;
    else
      for (int y = tHei / 4; y < tHei / 2; y++)
        for (int x = 0; x < tWid; x++)
          error += Math.pow(template[x][y] - ROI[ (y + y0) * sWid + (x + x0)], 2);

    if (error > min)
      return Double.MAX_VALUE;
    else
      for (int y = tHei / 2; y < 3*tHei / 4; y++)
        for (int x = 0; x < tWid; x++)
          error += Math.pow(template[x][y] - ROI[ (y + y0) * sWid + (x + x0)], 2);

    if (error > min)
      return Double.MAX_VALUE;
    else
      for (int y = 3*tHei / 4; y < tHei ; y++)
        for (int x = 0; x < tWid; x++)
          error += Math.pow(template[x][y] - ROI[ (y + y0) * sWid + (x + x0)], 2);
    return error;
  }
  ///////////////////////////////////////////

  public Point trackTemplate(int template[][],int ROI[],int sWid,int tWid,int sHei,int tHei)
  {
    double error,min = Double.MAX_VALUE;
    int nX = 0, nY = 0;
    for(int y=0 ; y<sHei-tHei ; y++)
      for(int x=0 ; x<sWid-tWid ; x++)
      {
        error = calculateSSD(template,ROI,tWid,sWid,x,y,min,tHei);
        if( error < min )
        {
          min = error;
          nX = x;
          nY = y;
        }
      }
    return new Point(nX+tWid/2,nY+tHei/2);
  }

  ///////////////////////////////////////////

  public Point findPupil(int ii[][], int secWid,int secHei,int template[][],
                         int ROI[],int tWid,int tHei)
  {
    int pX = 0, pY = 0, dX = 0, dY = 0, len = 7,sum,minSum = Integer.MAX_VALUE;
    for(int y=3 ; y<secHei-len-3 ; y++)
      for(int x=5 ; x<secWid-len-5 ; x++)
      {
        sum = ii[x+len][y+len]-ii[x][y+len]-ii[x+len][y]+ii[x][y];
        if( sum < minSum )
        {
          minSum = sum;
          dX = x+len/2;
          dY = y+len/2;
        }
      }
    double error,min = Double.MAX_VALUE;
    for(int y=0 ; y<secHei-tHei ; y++)
      for(int x=0 ; x<secWid-tWid ; x++)
      {
        error = calculateSSD(template,ROI,tWid,secWid,x,y,min,tHei);
        if( error < min )
        {
          min = error;
          pX = x+tWid/2;
          pY = y+tHei/2;
        }
      }
    return new Point((pX+dX)/2,(pY+dY)/2);
  }

  ///////////////////////////////////////////

  public void detectMotion(int oldPixels[], int newPixels[],int width, int height
                            ,int cThreshold,int mThreshold,boolean drawMotion,boolean drawEyes
                            ,ProcessEffect effect,int x0,int y0,int eye,int bThreshold
                            ,int nxShift,int nyShift,int bxShift,int byShift)
  {
    int counter = 0,pos;
    int limit = 1;
    //check for changes in pixels' intensities
    for (int y = 0; y < height; y++)
      for (int x = 0; x < width; x++)
        if (Math.abs(newPixels[y * width + x] - oldPixels[y * width + x]) > cThreshold)
        {
          counter++;
          tMotionPixels[y*width+x] = 1;
          if( drawMotion && drawEyes )
          {
            pos = (240 - (y0 + y)) * 960 + (x0 + x) * 3;
            if ( (pos >= 0) && (pos + 2 < effect.outData.length))
            {
              effect.outData[pos] = blue;
              pos++;
              effect.outData[pos] = green;
              pos++;
              effect.outData[pos] = red;
            }
          }
        }
        else
          tMotionPixels[y*width+x] = 0;

    //detect blinks
    if (counter > bThreshold)
      if (Math.abs(nxShift) < limit && Math.abs(nyShift) < limit && Math.abs(bxShift) < limit && Math.abs(byShift) < limit)
        switch (eye)
        {
          case 0:
            effect.leftBlink = true;
            break;
          case 1:
            effect.rightBlink = true;
            break;
        };
  }

  ///////////////////////////////////////////

  public int checkHorizontalBoundaries(int x,int wid,int shift,int len)
  {
    if (x - wid / 2 + shift < 0)
      return shift = wid / 2 - x;
    else
    if (x - wid / 2 + shift > len-wid)
      return shift = len - wid / 2 - x;
    return shift;
  }

  ////////////////////////////////////////////

  public int checkVerticalBoundaries(int y,int hei,int shift,int dep)
  {
    if (y - hei / 2 + shift < 0)
      return shift = hei/ 2 - y;
    else
    if (y - hei / 2 + shift > dep-hei)
      return shift = dep - hei/2  - y;
    return shift;
  }

  ////////////////////////////////////////////

  private BufferedImage constructEyeBrowROI(int lX,int lY,int rX,int rY,Image image,int index,double eyesLen,double slope,int threshold)
  {
    int sx,ex,sy,ey;
    int x0,y0;

    if( index == 0 ) //left eye
    {
      x0 = lX;
      y0 = lY;
    }
    else
    {
      x0 = rX;
      y0 = rY;
    }

    sx = x0 - (int) (eyesLen / 4d);
    ex = sx + (int) (0.5 * eyesLen * Math.cos(Math.atan(slope)));
    ey = y0 - 15;
    sy = ey - (int) ( (eyesLen / 4d) * Math.sin(Math.abs(Math.atan(slope))));

    int wid = ex-sx;
    int hei = ey-sy;
    if( hei < 20 )
    {
      ey += 5;
      sy -= 5;
    }
    if( hei > 30 )
    {
      ey -= 5;
      sy += 5;
    }
    hei = ey-sy;
    if( wid == 0 )
    {
      sx -= 5;
      ex += 5;
    }
    wid = ex-sx;
    int sec[] = new int[wid * hei];
    int gSec[] = new int[wid * hei];
    sec = ImageProcessing.extractPixels(image,sx,sy,wid,hei,sec);
    gSec = ImageProcessing.toGrayscale(sec,gSec);

    if(index == 0)
    {
      this.slx = sx;
      this.sly = sy;
    }
    else
    {
      this.srx = sx;
      this.sry = sy;
    }

    return ImageProcessing.toInvertedBinary(gSec,threshold,wid,hei);
  }

  ///////////////////////////////////////////

  public int[] findEyeBrowsLine(int lX,int lY,int rX,int rY,Image image,int index,double eyesLen,double slope,int threshold)
  {
    int coords[] = new int[6];
    int sx = 0,sy = 0,ex = 0,ey = 0;
    boolean found;
    BufferedImage ROI = constructEyeBrowROI(lX,lY,rX,rY,image,index,eyesLen,slope,threshold);
    if( ROI == null )
      return null;

    BufferedImage temp = new BufferedImage(ROI.getWidth(),ROI.getHeight(),BufferedImage.TYPE_INT_RGB);
    hough.run(ROI);
    ROI = hough.getSuperimposed(temp,1d);

    found = false;
    for(int x=0 ; x<ROI.getWidth()/2 && !found ; x++)
      for(int y=0 ; y<ROI.getHeight() ; y++)
        if(ROI.getRGB(x,y) != Color.black.getRGB())
        {
          sx = x;
          sy = y;
          found = true;
          break;
        }
    if( !found )
      return null;

    found = false;
    for(int x=ROI.getWidth()-1 ; x>=ROI.getWidth()/2 && !found ; x--)
      for(int y=ROI.getHeight()-1 ; y>=0 ; y--)
        if(ROI.getRGB(x,y) != Color.black.getRGB())
        {
          ex = x;
          ey = y;
          found = true;
          break;
        }
    if( !found )
      return null;

    coords[0] = sx;
    coords[1] = sy;
    coords[2] = ex;
    coords[3] = ey;

    if( index == 0 )
    {
      coords[4] = this.slx;
      coords[5] = this.sly;
    }
    else
    {
      coords[4] = this.srx;
      coords[5] = this.sry;
    }

    return coords;
  }


}
