package store.aurora.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.storage.entity.StorageInfo;


public interface StorageInfoRepository extends JpaRepository<StorageInfo, Long> {}

