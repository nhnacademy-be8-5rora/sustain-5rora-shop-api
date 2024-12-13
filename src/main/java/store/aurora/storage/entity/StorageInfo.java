package store.aurora.storage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "storage_infos")
@Getter
@NoArgsConstructor
public class StorageInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @NotBlank
    @Column(nullable = false, length = 100)
    private String storageName;

    @Setter
    @NotBlank
    @Column(nullable = false, unique = true)
    private String storageUrl;

//    @Setter
//    @OneToMany(mappedBy = "storageInfo", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<ReviewImage> reviewImages;

    public StorageInfo(String storageName, String storageUrl) {
        this.storageName = storageName;
        this.storageUrl = storageUrl;
    }
}
