package store.aurora.book.service.tag;


import store.aurora.book.dto.tag.TagRequestDto;
import store.aurora.book.dto.tag.TagResponseDto;

import java.util.List;

public interface TagService {

    TagResponseDto createTag(TagRequestDto requestDto);

    List<TagResponseDto> getAllTags();

    TagResponseDto getTagById(Long id);

    TagResponseDto updateTag(Long id, TagRequestDto requestDto);

    void deleteTag(Long id);
}
