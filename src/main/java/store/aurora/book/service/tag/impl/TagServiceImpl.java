package store.aurora.book.service.tag.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.dto.tag.BookTagRequestDto;
import store.aurora.book.dto.tag.TagRequestDto;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.tag.BookTag;
import store.aurora.book.entity.tag.Tag;
import store.aurora.book.exception.book.NotFoundBookException;
import store.aurora.book.exception.tag.AlreadyExistTagException;
import store.aurora.book.exception.tag.NotFoundBookTagException;
import store.aurora.book.exception.tag.NotFoundTagException;
import store.aurora.book.repository.BookRepository;
import store.aurora.book.repository.tag.BookTagRepository;
import store.aurora.book.repository.tag.TagRepository;
import store.aurora.book.service.tag.TagService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final BookTagRepository bookTagRepository;
    private final BookRepository bookRepository;

    @Override
    public void createTag(TagRequestDto requestDto) {
        Tag tag = new Tag(null, requestDto.name());
        tagRepository.save(tag);
    }

    @Override
    public void removeTag(Long tagId) {
        if (!tagRepository.existsById(tagId)) {
            throw new AlreadyExistTagException(tagId);
        }
        tagRepository.deleteById(tagId);
    }

    @Override
    public void addBookTag(BookTagRequestDto requestDto) {
        Long bookId = requestDto.bookId();
        Long tagId = requestDto.tagId();

        Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new NotFoundTagException(tagId));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new NotFoundBookException(bookId));

        BookTag bookTag = new BookTag(null, tag, book);

        bookTagRepository.save(bookTag);
    }

    @Override
    public void removeBookTag(Long bookTagId) {

        if (!bookTagRepository.existsById(bookTagId)) {
            throw new NotFoundBookTagException(bookTagId);
        }

        bookTagRepository.deleteById(bookTagId);
    }
}
