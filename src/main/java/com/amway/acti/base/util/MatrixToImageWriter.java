/**
 * Created by Will Zhang on 2018/2/11.
 */

package com.amway.acti.base.util;

import com.google.zxing.common.BitMatrix;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Component;

/**
 *  用于二维码的生成，由Google提供。
 *
 * Created by Eric on 2017/2/15.
 */
@Component
public final class MatrixToImageWriter
{

  private static final int BLACK = 0xFF000000;
  private static final int WHITE = 0xFFFFFFFF;

  private MatrixToImageWriter()
  {
  }

  /**
   * image格式转化
   * @param matrix
   * @return
   */
  public static BufferedImage toBufferedImage(BitMatrix matrix)
  {
    int width = matrix.getWidth();
    int height = matrix.getHeight();
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    for (int x = 0; x < width; x++)
    {
      for (int y = 0; y < height; y++)
      {
        image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
      }
    }

    return image;
  }

  /**
   * 文件写入
   * @param matrix
   * @param format
   * @param file
   * @param logoUrl
   * @throws IOException
   */
  public static void writeToFile(BitMatrix matrix, String format, File file,String logoUrl)
      throws IOException
  {
    File logoFile = new File(logoUrl);
    BufferedImage image = toBufferedImage(matrix);
    BufferedImage logo = ImageIO.read(logoFile);
    int deltaHeight = image.getHeight()-logo.getHeight();
    int deltaWidth = image.getWidth()-logo.getWidth();
    BufferedImage combined = new BufferedImage(image.getHeight(),image.getWidth(),BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) combined.getGraphics();
    g.drawImage(image, 0, 0, null);
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    g.drawImage(logo, (int) Math.round(deltaWidth / 2), (int) Math.round(deltaHeight / 2), null);
    if (!ImageIO.write(combined, format, file))
    {
      throw new IOException("Could not write an image of format " + format + " to " + file);
    }
  }

  /**
   * 流写入
   * @param matrix
   * @param format
   * @param stream
   * @throws IOException
   */
  public static void writeToStream(BitMatrix matrix, String format, OutputStream stream)
      throws IOException
  {
    BufferedImage image = toBufferedImage(matrix);
    if (!ImageIO.write(image, format, stream))
    {
      throw new IOException("Could not write an image of format " + format);
    }
  }
}
