package store.aurora.book.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.book.repository.AuthorRoleRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthorRoleService {
    private final AuthorRoleRepository authorRoleRepository;
}
