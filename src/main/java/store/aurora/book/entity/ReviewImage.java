package store.aurora.book.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "review_images")
public class ReviewImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @ManyToOne
    @JoinColumn(name = "storage_infos_id", nullable = false)
    private StorageInfo storageInfo;

    @Column(name = "image_file_path", nullable = false)
    private String imageFilePath;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "review_image_created_at")
    private Date createdAt;
}
