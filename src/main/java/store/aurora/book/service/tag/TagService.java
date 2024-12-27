package store.aurora.book.service.tag;


import store.aurora.book.dto.tag.BookTagRequestDto;
import store.aurora.book.dto.tag.TagRequestDto;
import store.aurora.book.dto.tag.TagResponseDto;

import java.util.List;

public interface TagService {
    void createTag(TagRequestDto requestDto);
    void removeTag(Long tagId);
    void addBookTag(BookTagRequestDto requestDto);
    void removeBookTag(Long bookTagId);

    List<TagResponseDto> getAllTags();
}
