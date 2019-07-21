package com.amway.acti.base.util;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

/**
 * <图片水印>
 * <功能详细描述>
 *
 * @author zhengweishun
 * @version [版本号, 2018/5/9]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@Slf4j
public class WaterMarkUtils {

    /**
     * @param srcImgPath 源图片路径
     * @param tarImgPath 保存的图片路径
     * @param waterMarkContent 水印内容
     * @param markContentColor 水印颜色
     * @param font 水印字体
     */
    public void addWaterMark(String srcImgPath, String tarImgPath, String waterMarkContent,Color markContentColor,Font font) {

        try {
            // 读取原图片信息
            File srcImgFile = new File(srcImgPath);//得到文件
            Image srcImg = ImageIO.read(srcImgFile);//文件转化为图片
            int srcImgWidth = srcImg.getWidth(null);//获取图片的宽
            int srcImgHeight = srcImg.getHeight(null);//获取图片的高
            // 加水印
            BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufImg.createGraphics();
            g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
            g.setColor(markContentColor); //根据图片的背景设置水印颜色
            g.setFont(font);              //设置字体

            //设置水印的坐标
//            int x = srcImgWidth - 2*getWatermarkLength(waterMarkContent, g);
//            int y = srcImgHeight - 2*getWatermarkLength(waterMarkContent, g);
            int x = 520;
            int y = 150;
            //画出水印
            g.drawString(waterMarkContent, x, y);
            g.dispose();
            // 输出图片
            FileOutputStream outImgStream = new FileOutputStream(tarImgPath);
            ImageIO.write(bufImg, "jpg", outImgStream);
            outImgStream.flush();
            outImgStream.close();

        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }
    public int getWatermarkLength(String waterMarkContent, Graphics2D g) {
        return g.getFontMetrics(g.getFont()).charsWidth(waterMarkContent.toCharArray(), 0, waterMarkContent.length());
    }
    public static void main(String[] args) {
        //水印字体
        Font font = new Font("微软雅黑", Font.PLAIN, 35);
        //源图片地址
        String srcImgPath="F://2.jpg";
        //待存储的地址
        String tarImgPath="F://6.jpg";
        //水印内容
        String waterMarkContent="杜凯";
        //水印图片色彩以及透明度
        Color color=new Color(0,0,0,128);
        new WaterMarkUtils().addWaterMark(srcImgPath, tarImgPath,waterMarkContent,color,font);
    }
}
