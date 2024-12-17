package store.aurora.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.repository.BookViewRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class BookViewService {
    private final BookViewRepository bookViewRepository;
}
