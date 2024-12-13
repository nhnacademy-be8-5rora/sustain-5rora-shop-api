package store.aurora.book.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.book.repository.BookViewRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class BookViewService {
    private final BookViewRepository bookViewRepository;
}
