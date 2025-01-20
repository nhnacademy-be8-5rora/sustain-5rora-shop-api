package store.aurora.book.repository.author;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import store.aurora.book.config.QuerydslConfiguration;
import store.aurora.book.entity.AuthorRole;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

@Import(QuerydslConfiguration.class)
@DataJpaTest
class AuthorRoleRepositoryTest {

    @Autowired
    private AuthorRoleRepository authorRoleRepository;

    @Test
    @DisplayName("역할 이름으로 존재 여부 확인")
    void testExistsByRole() {
        // Given
        AuthorRole role = new AuthorRole();
        role.setRole("지은이");
        authorRoleRepository.save(role);

        // When
        boolean exists = authorRoleRepository.existsByRole("지은이");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("역할 이름으로 검색")
    void testFindByRole() {
        // Given
        AuthorRole role = new AuthorRole();
        role.setRole("옮긴이");
        authorRoleRepository.save(role);

        // When
        Optional<AuthorRole> foundRole = authorRoleRepository.findByRole("옮긴이");

        // Then
        assertThat(foundRole).isPresent();
        assertThat(foundRole.get().getRole()).isEqualTo("옮긴이");
    }

    @Test
    @DisplayName("존재하지 않는 역할 조회 시 Optional.empty() 반환")
    void testFindByRoleNotFound() {
        // When
        Optional<AuthorRole> foundRole = authorRoleRepository.findByRole("없는 역할");

        // Then
        assertThat(foundRole).isEmpty();
    }

    @Test
    @DisplayName("ID 기준 정렬 후 페이징 조회 검증")
    void testFindAllByOrderByIdWithPagination() {
        // Given - 데이터 저장
        for (int i = 1; i <= 15; i++) {
            AuthorRole role = new AuthorRole();
            role.setRole("역할 " + i);
            authorRoleRepository.save(role);
        }

        Pageable pageable = PageRequest.of(0, 5); // 첫 번째 페이지, 5개씩 조회

        // When - 첫 번째 페이지 조회
        Page<AuthorRole> firstPage = authorRoleRepository.findAllByOrderById(pageable);

        // Then - 페이징 및 정렬 검증
        assertThat(firstPage).isNotEmpty();
        assertThat(firstPage.getContent()).hasSize(5);
        assertThat(firstPage.hasNext()).isTrue();
        assertThat(firstPage.getContent().getFirst().getRole()).isEqualTo("역할 1"); // ID 순 정렬 검증
    }

}