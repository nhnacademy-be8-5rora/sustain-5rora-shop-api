package store.aurora.book.service.tag.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.dto.tag.TagRequestDto;
import store.aurora.book.dto.tag.TagResponseDto;
import store.aurora.book.entity.tag.BookTag;
import store.aurora.book.entity.tag.Tag;
import store.aurora.book.repository.tag.BookTagRepository;
import store.aurora.book.repository.tag.TagRepository;
import store.aurora.book.service.tag.TagService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final BookTagRepository bookTagRepository;

    /**
     * 태그 생성
     */
    @Transactional
    @Override
    public TagResponseDto createTag(TagRequestDto requestDto) {
        if (tagRepository.findByName(requestDto.getName()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 태그입니다.");
        }
        Tag tag = new Tag();
        tag.setName(requestDto.getName());
        tag = tagRepository.save(tag);

        return mapToResponseDto(tag);
    }

    /**
     * 모든 태그 조회
     */
    @Override
    public List<TagResponseDto> getAllTags() {
        return tagRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    /**
     * ID로 태그 조회
     */
    @Override
    public TagResponseDto getTagById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("태그를 찾을 수 없습니다."));
        return mapToResponseDto(tag);
    }

    /**
     * 태그 수정
     */
    @Transactional
    @Override
    public TagResponseDto updateTag(Long id, TagRequestDto requestDto) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("태그를 찾을 수 없습니다."));
        tag.setName(requestDto.getName());
        tag = tagRepository.save(tag);
        return mapToResponseDto(tag);
    }

    /**
     * 태그 삭제
     */
    @Transactional
    @Override
    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new IllegalArgumentException("태그를 찾을 수 없습니다.");
        }
        tagRepository.deleteById(id);
    }

    @Transactional
    @Override
    public List<BookTag> createBookTags(List<Long> tagIds) {
        List<Tag> tags = tagRepository.findAllById(tagIds);
        return tags.stream()
                .map(tag -> {
                    BookTag bookTag = new BookTag();
                    bookTag.setTag(tag);
                    return bookTag;
                })
                .collect(Collectors.toList());
    }
    /**
     * 엔티티를 DTO로 매핑
     */
    private TagResponseDto mapToResponseDto(Tag tag) {
        TagResponseDto responseDto = new TagResponseDto();
        responseDto.setId(tag.getId());
        responseDto.setName(tag.getName());
        return responseDto;
    }
}
