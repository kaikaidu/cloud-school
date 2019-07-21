/**
 * Created by dk on 2018/6/6.
 */

package com.amway.acti.base.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class FileUtil {

    /**
     * 读取文件内容
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static String readfile(String filePath) throws IOException {
        File file = new File(filePath);
        InputStream input = null;
        try {
            input = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(),e);
        }
        StringBuffer buffer = new StringBuffer();
        byte[] bytes = new byte[1024];
        try {
            for (int n; (n = input.read(bytes)) != -1; ) {
                buffer.append(new String(bytes, 0, n, "utf-8"));
            }
        } catch (IOException ex) {
            log.info(ex.getMessage(), ex);
        } finally {
            if (null != input) {
                input.close();
            }
        }
        return buffer.toString();
    }

    // 删除zip 文件
    public static void delZipFile(String url) {
        File file = new File(url);
        file.delete();
    }

    // 删除解压后的文件
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

}
