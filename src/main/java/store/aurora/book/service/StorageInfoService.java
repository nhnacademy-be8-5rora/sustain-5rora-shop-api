package store.aurora.book.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.book.repository.StorageInfoRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class StorageInfoService {
    private final StorageInfoRepository storageInfoRepository;
}
