package store.aurora.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.review.entity.ReviewImage;
import store.aurora.storage.entity.StorageInfo;

public interface StorageInfoRepository extends JpaRepository<StorageInfo, Long> {
}
