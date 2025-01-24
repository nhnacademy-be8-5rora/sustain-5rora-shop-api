package store.aurora.common.advice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import store.aurora.book.exception.book.NotFoundBookImageException;
import store.aurora.book.exception.category.CategoryAlreadyExistException;
import store.aurora.book.exception.category.CategoryLimitException;
//import store.aurora.file.ObjectStorageException;
//import store.aurora.file.TokenRefreshException;
import store.aurora.user.exception.DormantAccountException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @RestController
    static class TestController {
        @GetMapping("/not-found")
        public void throwNotFoundException() {
            throw new NotFoundBookImageException("Data not found");
        }

        @GetMapping("/conflict")
        public void throwConflictException() {
            throw new CategoryAlreadyExistException("Data conflict");
        }

        @GetMapping("/bad-request")
        public void throwBadRequestException() {
            throw new CategoryLimitException("Limit exceeded");
        }

        @PostMapping("/enum-error")
        public void throwEnumParseError(@RequestBody String invalidEnum) {
            throw new HttpMessageNotReadableException("Cannot deserialize value of type");
        }

        @GetMapping("/forbidden")
        public void throwForbiddenException() {
            throw new DormantAccountException("Dormant account");
        }

//        @GetMapping("/object-storage-error")
//        public void throwObjectStorageException() {
//            throw new ObjectStorageException("Object storage error", HttpStatus.SERVICE_UNAVAILABLE);
//        }
//
//        @GetMapping("/unauthorized")
//        public void throwTokenRefreshException() {
//            throw new TokenRefreshException("Token refresh failed");
//        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Test
    void handleNotFoundExceptions() throws Exception {
        mockMvc.perform(get("/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMsg").value("Data not found"))
                .andExpect(jsonPath("$.httpStatus").value(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void handleDataConflictExceptions() throws Exception {
        mockMvc.perform(get("/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMsg").value("Data conflict"))
                .andExpect(jsonPath("$.httpStatus").value(HttpStatus.CONFLICT.value()));
    }

    @Test
    void handleBadRequestExceptions() throws Exception {
        mockMvc.perform(get("/bad-request"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMsg").value("Limit exceeded"))
                .andExpect(jsonPath("$.httpStatus").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void handleForbiddenException() throws Exception {
        mockMvc.perform(get("/forbidden"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Dormant account"));
    }

//    @Test
//    void handleObjectStorageException() throws Exception {
//        mockMvc.perform(get("/object-storage-error"))
//                .andExpect(status().isServiceUnavailable())
//                .andExpect(jsonPath("$.errorMsg").value("Object storage error"));
//    }
//
//    @Test
//    void handleTokenRefreshException() throws Exception {
//        mockMvc.perform(get("/unauthorized"))
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.errorMsg").value("Token refresh failed"));
//    }
}