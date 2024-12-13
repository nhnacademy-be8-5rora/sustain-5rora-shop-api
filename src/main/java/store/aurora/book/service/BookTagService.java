package store.aurora.book.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.book.repository.tag.BookTagRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class BookTagService {
    private final BookTagRepository bookTagRepository;
}
