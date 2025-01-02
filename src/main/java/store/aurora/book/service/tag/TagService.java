package store.aurora.book.service.tag;


import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.dto.tag.TagRequestDto;
import store.aurora.book.dto.tag.TagResponseDto;
import store.aurora.book.entity.tag.BookTag;

import java.util.List;

public interface TagService {

    TagResponseDto createTag(TagRequestDto requestDto);

    List<TagResponseDto> getAllTags();

    TagResponseDto getTagById(Long id);

    TagResponseDto updateTag(Long id, TagRequestDto requestDto);

    void deleteTag(Long id);

    List<BookTag> createBookTags(List<Long> tagIds);
}
