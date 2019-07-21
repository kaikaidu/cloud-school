package com.amway.acti.base.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * FTP服务器工具类  
 *
 * @author 码农先生
 * @version 1.0
 * @date 2013-11-18  
 * @see 参数 地址1：http://blog.csdn.net/hbcui1984/article/details/2720204  
 * @see 参数 地址2:http://blog.csdn.net/yibing548/article/details/38586073  
 * @see 参考地址3：http://blog.csdn.net/for_china2012/article/details/16820607  
 * @see 参考地址3：http://blog.csdn.net/kardelpeng/article/details/6588284  
 * @see 参考地址3：http://commons.apache.org/proper/commons-net/apidocs/org/apache/commons/net/ftp/FTPClient.html  
 *
 */
@SuppressWarnings("all")
@Slf4j
//@Component
public class FtpUtil {
    private static FtpUtil ftpUtils;
    private FTPClient ftpClient;

    //@Value("${ftp.serverUrl}")
    private String serverUrl; //服务器地址
    //@Value("${ftp.port}")
    private String port; // 服务器端口
    //@Value("${ftp.username}")
    private String username; // 用户登录名
    //@Value("${ftp.password}")
    private String password; // 用户登录密码


    /**
     * 连接（配置通用连接属性）至服务器  
     *
     * @param serverName
     *            服务器名称  
     * @param remotePath
     *            当前访问目录  
     * @return <b>true</b>：连接成功 <br/>  
     *         <b>false</b>：连接失败  
     */
    public boolean connectToTheServer(String remotePath) {
        // 定义返回值  
        boolean result = false;
        try {
            // 连接至服务器，端口默认为21时，可直接通过URL连接  
            ftpClient.connect(serverUrl, Integer.parseInt(port));
            // 登录服务器  
            ftpClient.login(username, password);
            // 判断返回码是否合法  
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                // 不合法时断开连接  
                ftpClient.disconnect();
                // 结束程序  
                return result;
            }
            //设置文件传输模式  
            //被动模式  
//          ftpClient.enterLocalPassiveMode();  
            //创建目录  
            ftpClient.makeDirectory(remotePath);
            // 设置文件操作目录  
            result = ftpClient.changeWorkingDirectory(remotePath);
            // 设置文件类型，二进制  
            result = ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            // 设置缓冲区大小  
            ftpClient.setBufferSize(3072);
            // 设置字符编码  
            ftpClient.setControlEncoding("UTF-8");

        } catch (IOException e) {
            log.error("连接FTP服务器异常",e);
            throw new RuntimeException("连接FTP服务器异常" , e);
        }
        return result;
    }

    /**
     * 上传文件至FTP服务器  
     *
     * @param serverName
     *            服务器名称  
     * @param storePath
     *            上传文件存储路径  
     * @param fileName
     *            上传文件存储名称  
     * @param is
     *            上传文件输入流  
     * @return <b>true</b>：上传成功 <br/>  
     *         <b>false</b>：上传失败  
     */
    public boolean storeFile( String storePath, String fileName, InputStream is) {
        boolean result = false;
        try {
            // 连接至服务器  
            result = connectToTheServer(storePath);
            // 判断服务器是否连接成功  
            if (result) {
                // 上传文件  
                result = ftpClient.storeFile(fileName, is);
            }
            // 关闭输入流  
            is.close();
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        } finally {
            // 判断输入流是否存在  
            if (null != is) {
                try {
                    // 关闭输入流  
                    is.close();
                } catch (IOException e) {
                    log.error("上传文件至FTP异常"+e.getMessage());
                    throw new RuntimeException("上传文件至FTP异常" , e);
                }
            }
            // 登出服务器并断开连接  
            ftpUtils.logout();
        }
        return result;
    }

    /**
     * Description: 从FTP服务器下载文件  
     *
     * @Version1.0 Jul 27, 2008 5:32:36 PM by 崔红保（cuihongbao@d-heaven.com）创建  
     * @param remotePath
     *            FTP服务器上的相对路径  
     * @param fileName
     *            要下载的文件名  
     * @param localPath
     *            下载后保存到本地的路径  
     * @return
     */
    public boolean retrieveFile(String remotePath, String fileName,String localPath) {
        // 初始表示下载失败  
        boolean success = false;
        //表示是否连接成功  
        boolean result = false;
        File file = new File(localPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            // 连接至服务器  
            result = connectToTheServer(remotePath);
            if(result){
                // 列出该目录下所有文件  
                FTPFile[] fs = ftpClient.listFiles();
                // 遍历所有文件，找到指定的文件  
                for (FTPFile ff : fs) {
                    if (ff.getName().equals(fileName)) {
                        // 根据绝对路径初始化文件  
                        File localFile = new File(localPath + "/" + ff.getName());
                        //输出流  
                        OutputStream is = new FileOutputStream(localFile);
                        //下载文件  
                        success=ftpClient.retrieveFile(ff.getName(), is);
                        is.close();
                    }
                }
            }

        } catch (IOException e) {
            log.error("从FTP服务器下载文件异常",e);
        } finally {
            // 登出服务器并断开连接  
            ftpUtils.logout();
        }
        return success;
    }


    /**
     * 删除FTP服务器文件  
     *
     * @param serverName
     *            服务器名称  
     * @param remotePath
     *            当前访问目录  
     * @param fileName
     *            文件存储名称  
     * @return <b>true</b>：删除成功 <br/>  
     *         <b>false</b>：删除失败  
     */
    public boolean deleteFile(String remotePath,String fileName) {
        boolean result = false;
        // 连接至服务器  
        result = connectToTheServer(remotePath);
        // 判断服务器是否连接成功  
        if (result) {
            try {
                // 删除文件  
                result = ftpClient.deleteFile(fileName);
            } catch (IOException e) {
                log.error("删除FTP服务器上的 文件异常"+e.getMessage());
                throw new RuntimeException("删除FTP服务器上的 文件异常" , e);
            } finally {
                // 登出服务器并断开连接  
                ftpUtils.logout();
            }
        }
        return result;
    }

    /**
     * 检测FTP服务器文件是否存在  
     *
     * @param serverName
     *            服务器名称  
     * @param remotePath
     *            检测文件存储路径  
     * @param fileName
     *            检测文件存储名称  
     * @return <b>true</b>：文件存在 <br/>  
     *         <b>false</b>：文件不存在  
     */
    public boolean checkFile( String remotePath,String fileName) {
        boolean result = false;
        try {
            // 连接至服务器  
            result = connectToTheServer(remotePath);
            // 判断服务器是否连接成功  
            if (result) {
                // 默认文件不存在  
                result = false;
                // 获取文件操作目录下所有文件名称  
                String[] remoteNames = ftpClient.listNames();
                // 循环比对文件名称，判断是否含有当前要下载的文件名  
                for (String remoteName : remoteNames) {
                    if (fileName.equals(remoteName)) {
                        result = true;
                    }
                }
            }
        } catch (IOException e) {
            log.error("检查FTP文件是否存在异常"+e.getMessage());
            throw new RuntimeException("检查FTP文件是否存在异常" , e);
        } finally {
            // 登出服务器并断开连接  
            ftpUtils.logout();
        }
        return result;
    }

    /**
     * 登出服务器并断开连接  
     *
     * @param ftp
     *            FTPClient对象实例  
     * @return <b>true</b>：操作成功 <br/>  
     *         <b>false</b>：操作失败  
     */
    public boolean logout() {
        boolean result = false;
        if (null != ftpClient) {
            try {
                // 登出服务器  
                result = ftpClient.logout();
            } catch (IOException e) {
                log.error("登录FTP服务器异常"+e.getMessage());
                throw new RuntimeException("登录FTP服务器异常" , e);
            } finally {
                // 判断连接是否存在  
                if (ftpClient.isConnected()) {
                    try {
                        // 断开连接  
                        ftpClient.disconnect();
                    } catch (IOException e) {
                        log.error("关闭FTP服务器异常"+e.getMessage());
                        throw new RuntimeException("关闭FTP服务器异常" , e);
                    }
                }
            }
        }
        return result;
    }





    /**
     * Description: 从FTP服务器下载文件  
     *
     * @param url
     *            FTP服务器hostname  
     * @param port
     *            FTP服务器端口  
     * @param username
     *            FTP登录账号  
     * @param password
     *            FTP登录密码  
     * @param remotePath
     *            FTP服务器上的相对路径  
     * @param fileName
     *            要下载的文件名  
     * @param localPath
     *            下载后保存到本地的路径  
     * @return
     */
    public static boolean downFile(String url, int port, String username,
                                   String password, String remotePath, String fileName,
                                   String localPath) {
        // 初始表示下载失败  
        boolean success = false;
        // 创建FTPClient对象  
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            // 连接FTP服务器  
            // 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器  
            ftp.connect(url, port);
            reply = ftp.getReplyCode();
            /*
             * 判断是否连接成功
             */
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return success;
            } else {
                // 登录ftp  
                if (ftp.login(username, password)) {
                    // 转到指定下载目录  
                    ftp.changeWorkingDirectory(remotePath);
                    // 列出该目录下所有文件  
                    FTPFile[] fs = ftp.listFiles();
                    // 遍历所有文件，找到指定的文件  
                    for (FTPFile ff : fs) {
                        if (ff.getName().equals(fileName)) {
                            // 根据绝对路径初始化文件  
                            File localFile = new File(localPath + "/"
                                    + ff.getName());
                            // 输出流  
                            OutputStream is = new FileOutputStream(localFile);
                            // 下载文件  
                            ftp.retrieveFile(ff.getName(), is);
                            is.close();
                        }
                    }
                    // 退出ftp  
                    ftp.logout();
                    // 下载成功  
                    success = true;
                }
            }
        } catch (IOException e) {
            log.error("从FTP服务器下载文件异常"+e.getMessage());
            throw new RuntimeException("从FTP服务器下载文件异常" , e);
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    log.error("关闭FTP连接异常"+ioe.getMessage());
                    throw new RuntimeException("关闭FTP连接异常" , ioe);
                }
            }
        }
        return success;
    }


}  
