package store.aurora.book.util;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import store.aurora.book.entity.AuthorRole;
import store.aurora.book.repository.AuthorRoleRepository;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class AuthorRoleInitializer implements CommandLineRunner {
    private final AuthorRoleRepository authorRoleRepository;

    @Override
    public void run(String... args) {
        Arrays.stream(AuthorRole.Role.values())
                .forEach(role -> {
                    if (!authorRoleRepository.existsByRole(role)) {
                        authorRoleRepository.save(new AuthorRole(null, role));
                    }
                });
    }
}
