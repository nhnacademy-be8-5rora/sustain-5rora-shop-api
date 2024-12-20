//package store.aurora.file;
//
//import com.nhncloud.sdk.objectstorage.NhnCloudObjectStorage;
//import com.nhncloud.sdk.objectstorage.model.PutObjectRequest;
//import com.nhncloud.sdk.objectstorage.model.DeleteObjectRequest;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.annotation.PostConstruct;
//import java.io.IOException;
//import java.util.UUID;
//
//@Service
//@Profile("prod")
//public class NhnObjectStorageServiceImpl implements FileStorageService {
//
//    @Value("${storage.nhn.bucket-name}")
//    private String bucketName;
//    @Value("${storage.nhn.endpoint}")
//    private String endpoint;
//    @Value("${storage.nhn.access-key}")
//    private String accessKey;
//    @Value("${storage.nhn.secret-key}")
//    private String secretKey;
//
//    private NhnCloudObjectStorage nhnCloudObjectStorage;
//
//    @PostConstruct
//    public void initializeObjectStorage() {
//        nhnCloudObjectStorage = new NhnCloudObjectStorage(endpoint, accessKey, secretKey);
//    }
//
//    @Override
//    public String uploadFile(MultipartFile file, String directory) throws IOException {
//        // 파일 경로: Books/{bookId}/
//        String filename = directory + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
//
//        nhnCloudObjectStorage.putObject(new PutObjectRequest()
//                .withBucketName(bucketName)
//                .withObjectName(filename)
//                .withInputStream(file.getInputStream())
//                .withContentLength(file.getSize())
//                .withContentType(file.getContentType()));
//
//        return endpoint + "/" + bucketName + "/" + filename; // 저장된 파일 URL 반환
//    }
//
//    @Override
//    public void deleteFile(String filePath) throws IOException {
//        String key = filePath.replace(endpoint + "/" + bucketName + "/", "");
//        nhnCloudObjectStorage.deleteObject(new DeleteObjectRequest()
//                .withBucketName(bucketName)
//                .withObjectName(key));
//    }
//
//    @Override
//    public String getFileUrl(String filePath) {
//        return filePath; // 오브젝트 저장소 URL 반환
//    }
//}