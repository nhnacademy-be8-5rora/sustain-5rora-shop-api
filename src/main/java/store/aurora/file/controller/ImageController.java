package store.aurora.file.controller;

import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.aurora.file.service.ImageService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/{type}/{id}/{filename}")
    public ResponseEntity<Resource> getImage(
            @PathVariable String type,
            @PathVariable Long id,
            @PathVariable String filename) {

        try {
            Path imagePath = Paths.get(imageService.getStorageRoot(), type, id.toString(), filename);
            System.out.println("Loading image from path: " + imagePath.toString());

            if (!Files.exists(imagePath) || !Files.isReadable(imagePath)) {
                System.out.println("File not found: " + imagePath);
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(imagePath.toUri());

            return ResponseEntity.ok()
                    .contentType(getContentType(imagePath))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    private MediaType getContentType(Path filePath) {
        try {
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                String fileName = filePath.getFileName().toString().toLowerCase();
                if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                    return MediaType.IMAGE_JPEG;
                } else if (fileName.endsWith(".png")) {
                    return MediaType.IMAGE_PNG;
                } else if (fileName.endsWith(".gif")) {
                    return MediaType.IMAGE_GIF;
                }
            } else {
                return MediaType.parseMediaType(contentType);
            }
        } catch (IOException e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
