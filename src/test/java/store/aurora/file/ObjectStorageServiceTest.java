package store.aurora.file;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObjectStorageServiceTest {

//    @InjectMocks
//    private ObjectStorageService objectStorageService;
//
//    @Mock
//    private RestTemplate restTemplate;
//
//    @Mock
//    private TokenManager tokenManager;
//
//    @Value("${nhncloud.storage.url}")
//    private String storageUrl = "http://mock-storage.com";
//
//    @Value("${nhncloud.storage.container}")
//    private String containerName = "mock-container";
//
//    private MockMultipartFile mockFile;
//
//    @BeforeEach
//    void setUp() {
//        mockFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());
//    }
//
//    @Test
//    void testUploadObject_FileIsEmpty() {
//        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.jpg", "image/jpeg", new byte[0]);
//
//        ObjectStorageException exception = assertThrows(ObjectStorageException.class, () -> {
//            objectStorageService.uploadObject(emptyFile);
//        });
//
//        assertEquals("파일 업로드 실패: 업로드할 파일이 비어 있거나 존재하지 않습니다.", exception.getMessage());
//    }
//
//    @Test
//    void testUploadObjectFromUrl_InvalidUrl() {
//        String invalidUrl = "";
//
//        ObjectStorageException exception = assertThrows(ObjectStorageException.class, () -> {
//            objectStorageService.uploadObjectFromUrl(invalidUrl);
//        });
//
//        assertEquals("파일 업로드 실패: 유효하지 않은 이미지 URL입니다.", exception.getMessage());
//    }
//
//    @Test
//    void testDeleteObject_EmptyUrl() {
//        objectStorageService.deleteObject("");
//        verify(restTemplate, never()).execute(any(URI.class), eq(HttpMethod.DELETE), any(), any());
//    }
}