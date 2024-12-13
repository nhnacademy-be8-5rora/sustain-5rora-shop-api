package store.aurora.book.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.book.repository.BookAuthorRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class BookAuthorService {
    private final BookAuthorRepository bookAuthorRepository;
}
