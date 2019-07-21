package com.amway.acti.base.util;


import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;


public class QrCodeCreateUtil {

    /**
     * 生成图像
     *
     * @throws WriterException
     * @throws IOException
     */
    public void codeCreate(String courseId) throws WriterException, IOException {
//        String filePathClass =this.getClass().getResource("/").getPath()+ "assets/images/QRCode"; //编译后文件夹
        String filePath = "C:\\tomcat_server\\apache-tomcat-8.0.39-windows-x64\\apache-tomcat-8.0.39\\webapps\\cloudschool\\img";
        String fileName = "code"+courseId+".jpg";
        String url = "https://cstest.connext.com.cn/frontend/register?courseId="+courseId;
        int width = 200; // 图像宽度
        int height = 200; // 图像高度
        String format = "png";// 图像类型
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = new MultiFormatWriter().encode(url,BarcodeFormat.QR_CODE, width, height, hints);// 生成矩阵
        Path path = FileSystems.getDefault().getPath(filePath, fileName);
        MatrixToImageWriter.writeToPath(bitMatrix, format, path);// 输出图像
    }

    /**
     * 测试方法
     * @param args
     * @throws IOException
     * @throws WriterException
     */
    /*public static void main(String[] args) throws IOException, WriterException {
        QrCodeCreateUtil q = new QrCodeCreateUtil();
        q.codeCreate("162");
    }*/
}
