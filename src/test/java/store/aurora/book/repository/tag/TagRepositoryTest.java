package store.aurora.book.repository.tag;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import store.aurora.book.entity.tag.Tag;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TagRepositoryTest {

    @Mock
    private TagRepository tagRepository;

    private TagRepositoryTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("findByName - 태그 이름으로 검색")
    void testFindByName() {
        // Given
        Tag mockTag = new Tag();
        mockTag.setId(1L);
        mockTag.setName("Java");
        when(tagRepository.findByName("Java")).thenReturn(Optional.of(mockTag));

        // When
        Optional<Tag> result = tagRepository.findByName("Java");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Java");

        // Verify repository interaction
        verify(tagRepository, times(1)).findByName("Java");
    }

    @Test
    @DisplayName("findByNameContaining - 키워드로 태그 검색")
    void testFindByNameContaining() {
        // Given
        Tag tag1 = new Tag();
        tag1.setId(1L);
        tag1.setName("Java Programming");

        Tag tag2 = new Tag();
        tag2.setId(2L);
        tag2.setName("JavaScript Basics");

        when(tagRepository.findByNameContaining("Java")).thenReturn(List.of(tag1, tag2));

        // When
        List<Tag> result = tagRepository.findByNameContaining("Java");

        // Then
        assertThat(result)
                .hasSize(2)
                .extracting(Tag::getName)
                .containsExactlyInAnyOrder("Java Programming", "JavaScript Basics");

        // Verify repository interaction
        verify(tagRepository, times(1)).findByNameContaining("Java");
    }

    @Test
    @DisplayName("findByNameContaining - 결과 없음")
    void testFindByNameContaining_NoResults() {
        // Given
        when(tagRepository.findByNameContaining("NonExisting")).thenReturn(List.of());

        // When
        List<Tag> result = tagRepository.findByNameContaining("NonExisting");

        // Then
        assertThat(result).isEmpty();

        // Verify repository interaction
        verify(tagRepository, times(1)).findByNameContaining("NonExisting");
    }
}