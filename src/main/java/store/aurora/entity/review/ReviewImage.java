package store.aurora.entity.review;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;
import store.aurora.entity.StorageInfo;

import java.time.LocalDateTime;

@Entity
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

    @Setter
    @NotNull
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "storage_infos_id", nullable = false)
    private StorageInfo storageInfo;
}
