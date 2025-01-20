package store.aurora.book.repository.author;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import store.aurora.book.config.QuerydslConfiguration;
import store.aurora.book.entity.Author;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

@Import(QuerydslConfiguration.class)
@DataJpaTest
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    @DisplayName("저자 이름으로 존재 여부 확인")
    void testExistsByName() {
        // Given
        Author author = new Author();
        author.setName("한강");
        authorRepository.save(author);

        // When
        boolean exists = authorRepository.existsByName("한강");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("저자 이름으로 검색")
    void testFindByName() {
        // Given
        Author author = new Author();
        author.setName("김영한");
        authorRepository.save(author);

        // When
        Optional<Author> foundAuthor = authorRepository.findByName("김영한");

        // Then
        assertThat(foundAuthor).isPresent();
        assertThat(foundAuthor.get().getName()).isEqualTo("김영한");
    }

    @Test
    @DisplayName("존재하지 않는 작가 조회 시 Optional.empty() 반환")
    void testFindByNameNotFound() {
        // When
        Optional<Author> foundName = authorRepository.findByName("없는 작가");

        // Then
        assertThat(foundName).isEmpty();
    }
}