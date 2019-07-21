package com.amway.acti.base.util;
import com.amway.acti.base.property.StorageConfig;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.SharedAccessBlobPermissions;
import com.microsoft.azure.storage.blob.SharedAccessBlobPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;

@Component
public class AzureBlobUploadUtil {

    @Autowired
    StorageConfig storageConfig;

    private static String STORAGE_STR = null;

    private static final String CONTAINER_NAME = "ContainerName";

    private static final String SHARED_ACCESS = "SharedAccess";

    private static final String MINUTE = "Minute";

    private static CloudBlobClient clent = null;


    /**
     *  微软云上传
     * @param inputStream
     * @param fileFullName
     * @param fileLength
     * @param containerName
     */
    public String upload(InputStream inputStream, String fileFullName, long fileLength, String containerName)
            throws URISyntaxException, InvalidKeyException, StorageException, IOException {
        if (null == STORAGE_STR) {
            STORAGE_STR = storageConfig.getDefaultEndpointsProtocol()
                    + storageConfig.getAccountName()
                    + storageConfig.getAccountKey();
        }
        String containerNameKey = containerName + CONTAINER_NAME;
        String containerSharedAccessKey = containerName + SHARED_ACCESS;
        String containerMinuteKey = containerName + MINUTE;
        Map<String, String> containerMap = storageConfig.getContainers();

        String name = containerMap.get(containerNameKey);
        if (null != containerMap.get(containerSharedAccessKey)) {
            boolean sharedAccess = Boolean.parseBoolean(containerMap.get(containerSharedAccessKey));
            if (sharedAccess) {
                int minute = Integer.parseInt(containerMap.get(containerMinuteKey));
                return GetBlobUrl(inputStream, fileFullName, fileLength, name, minute, STORAGE_STR);
            }
        }
        return getUrl(inputStream, fileFullName, fileLength, name, STORAGE_STR);
    }


    /**
     * 返回公共 url
     *
     * @param inputStream
     * @param fileFullName
     * @param fileLength
     * @param containerName
     */
    private static String getUrl(InputStream inputStream, String fileFullName, long fileLength, String containerName, String storageStr) throws URISyntaxException, InvalidKeyException, StorageException, IOException {
        CloudBlobClient serviceClient = AzureBlobUploadUtil.getCloudBlobClient(storageStr);
        CloudBlobContainer container = serviceClient.getContainerReference(containerName);
        container.createIfNotExists();
        CloudBlockBlob blob = container.getBlockBlobReference(fileFullName);
        blob.upload(inputStream, fileLength);
        BlobContainerPermissions containerPermissions = new BlobContainerPermissions();
        containerPermissions.setPublicAccess(BlobContainerPublicAccessType.BLOB);
        container.uploadPermissions(containerPermissions);
        if (null != blob.getUri()) {
            return blob.getUri().toString();
        }
        return null;
    }

    /**
     * 删除
     * @param fileFullName 文件名称
     * @param containerName 容器名称
     * @throws URISyntaxException
     * @throws InvalidKeyException
     * @throws StorageException
     * @throws IOException
     */
    public void delete(String fileFullName, String containerName) throws URISyntaxException, InvalidKeyException, StorageException, IOException {
        if (null == STORAGE_STR) {
            STORAGE_STR = storageConfig.getDefaultEndpointsProtocol()
                + storageConfig.getAccountName()
                + storageConfig.getAccountKey();
        }
        CloudBlobClient serviceClient = AzureBlobUploadUtil.getCloudBlobClient(STORAGE_STR);
        CloudBlobContainer container = serviceClient.getContainerReference(containerName);
        container.createIfNotExists();
        CloudBlockBlob blob = container.getBlockBlobReference(fileFullName);
        blob.delete();
    }

    /***
     * 返回签名 url
     * @param inputStream
     * @param fileFullName
     * @param fileLength
     * @param containerName
     */
    private static String GetBlobUrl(InputStream inputStream, String fileFullName, long fileLength, String containerName, int minute, String storageStr) throws StorageException, InvalidKeyException, URISyntaxException, IOException {
        CloudBlobClient serviceClient = AzureBlobUploadUtil.getCloudBlobClient(storageStr);
        CloudBlobContainer container = serviceClient.getContainerReference(containerName);
        container.createIfNotExists();
        CloudBlockBlob blob = container.getBlockBlobReference(fileFullName);
        blob.upload(inputStream, fileLength);

        SharedAccessBlobPolicy policy = new SharedAccessBlobPolicy();
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        calendar.setTime(new Date());
        // Immediately applicable
        policy.setSharedAccessStartTime(calendar.getTime());
        // Applicable time span is 1 hour
        calendar.add(Calendar.MINUTE, minute);
        policy.setSharedAccessExpiryTime(calendar.getTime());
        // SAS grants READ access privileges
        policy.setPermissions(EnumSet.of(SharedAccessBlobPermissions.READ));
        BlobContainerPermissions containerPermissions = new BlobContainerPermissions();
        // Private blob-container with no access for anonymous users
        containerPermissions.setPublicAccess(BlobContainerPublicAccessType.OFF);
        container.uploadPermissions(containerPermissions);
        String sas = container.generateSharedAccessSignature(policy, null);
        String blobUri = blob.getUri().toString();
        return blobUri + "?" + sas;
    }

    /**
     * 获取CloudBlobClient
     * @param storageStr
     */
    public static CloudBlobClient getCloudBlobClient(String storageStr) throws URISyntaxException, InvalidKeyException {
        if (null == clent) {
            CloudStorageAccount account = CloudStorageAccount.parse(storageStr);
            clent = account.createCloudBlobClient();
        }
        return clent;
    }

}
