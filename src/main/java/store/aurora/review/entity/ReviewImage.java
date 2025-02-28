package store.aurora.review.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import store.aurora.storage.entity.StorageInfo;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "review_images")
public class ReviewImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @NotNull
    @Column(nullable = false)
    private LocalDateTime reviewImageCreatedAt = LocalDateTime.now();

    @Setter
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Review review;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String imageFilePath;

    // todo
    @Setter
    //@NotNull
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "storage_infos_id")//, nullable = false)
    private StorageInfo storageInfo;
}
