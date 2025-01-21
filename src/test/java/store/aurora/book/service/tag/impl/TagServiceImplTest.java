package store.aurora.book.service.tag.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import store.aurora.book.dto.tag.TagRequestDto;
import store.aurora.book.dto.tag.TagResponseDto;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.tag.BookTag;
import store.aurora.book.entity.tag.Tag;
import store.aurora.book.exception.tag.TagAlreadyExistException;
import store.aurora.book.exception.tag.TagNotFoundException;
import store.aurora.book.parser.TagParser;
import store.aurora.book.repository.tag.BookTagRepository;
import store.aurora.book.repository.tag.TagRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TagServiceImplTest {

    @InjectMocks
    private TagServiceImpl tagService;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private BookTagRepository bookTagRepository;

    @Mock
    private TagParser tagParser;

    private Tag testTag;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testTag = new Tag("테스트 태그");
    }


    @Test
    @DisplayName("모든 태그 목록을 페이지네이션으로 조회")
    void testGetTags() {
        // Given
        Pageable pageable = mock(Pageable.class);
        List<Tag> tags = List.of(testTag);
        when(tagRepository.findAllByOrderById(pageable)).thenReturn(new PageImpl<>(tags));

        // When
        Page<TagResponseDto> result = tagService.getTags(pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getName()).isEqualTo("테스트 태그");
    }

    @Test
    @DisplayName("ID로 태그 조회")
    void testGetTagById() {
        // Given
        when(tagRepository.findById(1L)).thenReturn(Optional.of(testTag));

        // When
        TagResponseDto response = tagService.getTagById(1L);

        // Then
        assertThat(response.getName()).isEqualTo("테스트 태그");
    }

    @Test
    @DisplayName("존재하지 않는 태그 조회 시 예외 발생")
    void testGetTagById_NotFound() {
        // Given
        when(tagRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tagService.getTagById(1L))
                .isInstanceOf(TagNotFoundException.class);
    }

    @Test
    @DisplayName("새로운 태그 추가")
    void testCreateTag() {
        // Given
        TagRequestDto request = new TagRequestDto("새 태그");
        when(tagRepository.findByName(request.getName())).thenReturn(Optional.empty());

        when(tagRepository.save(any(Tag.class))).thenReturn(new Tag(1L, "새 태그"));

        // When
        TagResponseDto result = tagService.createTag(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("새 태그");
    }

    @Test
    @DisplayName("중복된 태그 추가 시 예외 발생")
    void testCreateTag_Duplicate() {
        // Given
        TagRequestDto request = new TagRequestDto("테스트 태그");
        when(tagRepository.findByName(request.getName())).thenReturn(Optional.of(testTag));

        // When & Then
        assertThatThrownBy(() -> tagService.createTag(request))
                .isInstanceOf(TagAlreadyExistException.class);
    }

    @Test
    @DisplayName("태그 수정 성공")
    void testUpdateTag_Success() {
        // Given
        TagRequestDto updateRequest = new TagRequestDto("수정된 태그");
        when(tagRepository.findById(1L)).thenReturn(Optional.of(testTag));
        when(tagRepository.findByName(updateRequest.getName())).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(new Tag(1L, "수정된 태그"));

        // When
        TagResponseDto result = tagService.updateTag(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("수정된 태그");
        verify(tagRepository, times(1)).save(testTag);
    }

    @Test
    @DisplayName("존재하지 않는 태그 수정 시 예외 발생")
    void testUpdateTag_NotFound() {
        // Given
        TagRequestDto updateRequest = new TagRequestDto("수정된 태그");
        when(tagRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tagService.updateTag(1L, updateRequest))
                .isInstanceOf(TagNotFoundException.class);
    }

    @Test
    @DisplayName("중복된 태그 이름으로 수정 시 예외 발생")
    void testUpdateTag_DuplicateName() {
        // Given
        TagRequestDto updateRequest = new TagRequestDto("테스트 태그");
        when(tagRepository.findById(1L)).thenReturn(Optional.of(testTag));
        when(tagRepository.findByName(updateRequest.getName())).thenReturn(Optional.of(testTag));

        // When & Then
        assertThatThrownBy(() -> tagService.updateTag(1L, updateRequest))
                .isInstanceOf(TagAlreadyExistException.class);
    }

    @Test
    @DisplayName("태그 삭제")
    void testDeleteTag() {
        // Given
        when(tagRepository.findById(1L)).thenReturn(Optional.of(testTag));

        // When
        tagService.deleteTag(1L);

        // Then
        verify(tagRepository, times(1)).delete(testTag);
    }

    @Test
    @DisplayName("존재하지 않는 태그 삭제 시 예외 발생")
    void testDeleteTag_NotFound() {
        // Given
        when(tagRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tagService.deleteTag(1L))
                .isInstanceOf(TagNotFoundException.class);
    }

    @Test
    @DisplayName("책의 태그 정보를 포맷팅된 문자열로 반환")
    void testGetFormattedTags() {
        // Given
        Book testBook = new Book();
        BookTag bookTag = new BookTag(1L, testTag, testBook);
        List<BookTag> bookTags = List.of(bookTag);

        when(bookTagRepository.findByBook(testBook)).thenReturn(bookTags);
        when(tagParser.formatTags(bookTags)).thenReturn("테스트 태그");

        // When
        String result = tagService.getFormattedTags(testBook);

        // Then
        assertThat(result).isEqualTo("테스트 태그");
        verify(tagParser, times(1)).formatTags(bookTags);
    }

    @Test
    @DisplayName("기존 태그는 반환하고, 존재하지 않는 태그는 새로 생성")
    void testGetOrCreateTagsByName() {
        // Given
        String tagString = "테스트 태그, 새 태그";
        List<String> parsedTags = List.of("테스트 태그", "새 태그");

        when(tagParser.parseTags(tagString)).thenReturn(parsedTags);
        when(tagRepository.findByName("테스트 태그")).thenReturn(Optional.of(testTag));
        when(tagRepository.findByName("새 태그")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(new Tag(2L, "새 태그"));

        // When
        List<Tag> result = tagService.getOrCreateTagsByName(tagString);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("테스트 태그");
        assertThat(result.get(1).getName()).isEqualTo("새 태그");
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    @DisplayName("빈 문자열이 입력될 경우 빈 리스트 반환")
    void testGetOrCreateTagsByName_EmptyString() {
        // Given
        String emptyTags = "";

        // When
        List<Tag> result = tagService.getOrCreateTagsByName(emptyTags);

        // Then
        assertThat(result).isEmpty();
        verify(tagRepository, never()).findByName(anyString());
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    @DisplayName("null이 입력될 경우 빈 리스트 반환")
    void testGetOrCreateTagsByName_NullInput() {

        // When
        List<Tag> result = tagService.getOrCreateTagsByName(null);

        // Then
        assertThat(result).isEmpty();
        verify(tagRepository, never()).findByName(anyString());
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    @DisplayName("태그 리스트를 BookTag 리스트로 변환")
    void testCreateBookTags() {
        // Given
        Tag tag1 = new Tag(1L, "베스트셀러");
        Tag tag2 = new Tag(2L, "신간");

        List<Tag> tags = List.of(tag1, tag2);

        // When
        List<BookTag> bookTags = tagService.createBookTags(tags);

        // Then
        assertThat(bookTags).hasSize(2);
        assertThat(bookTags.get(0).getTag()).isEqualTo(tag1);
        assertThat(bookTags.get(1).getTag()).isEqualTo(tag2);
    }

    @Test
    @DisplayName("빈 태그 리스트 입력 시 빈 리스트 반환")
    void testCreateBookTags_EmptyList() {
        // Given
        List<Tag> tags = List.of();

        // When
        List<BookTag> bookTags = tagService.createBookTags(tags);

        // Then
        assertThat(bookTags).isEmpty();
    }
}