package store.aurora.book.service.tag;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.dto.tag.TagRequestDto;
import store.aurora.book.dto.tag.TagResponseDto;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.tag.BookTag;
import store.aurora.book.entity.tag.Tag;

import java.util.List;

public interface TagService {

    TagResponseDto createTag(TagRequestDto requestDto);

    List<TagResponseDto> searchTags(String keyword);

    List<TagResponseDto> getAllTags();

    Page<TagResponseDto> getAllTags(Pageable pageable);

    TagResponseDto getTagById(Long id);

    TagResponseDto updateTag(Long id, TagRequestDto requestDto);

    void deleteTag(Long id);

    List<Tag> getOrCreateTagsByName(String tags);

    List<BookTag> createBookTags(List<Tag> tags);

    String getFormattedTags(Book book);
}
