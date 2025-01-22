package store.aurora.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import store.aurora.book.entity.Book;
import store.aurora.book.entity.BookImage;
import store.aurora.book.entity.category.BookCategory;
import store.aurora.book.entity.category.Category;

import store.aurora.book.repository.author.BookAuthorRepository;
import store.aurora.book.repository.book.BookRepository;
import store.aurora.book.repository.category.BookCategoryRepository;
import store.aurora.book.repository.category.CategoryRepository;
import store.aurora.book.service.image.BookImageService;
import store.aurora.document.*;
import store.aurora.search.dto.BookSearchResponseDTO;
import store.aurora.search.repository.ElasticSearchRepository;
import store.aurora.search.service.ElasticSearchService;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ElasticSearchServiceImpl implements ElasticSearchService {

    private final ElasticSearchRepository elasticSearchRepository;
    private final BookImageService bookImageService;
    private final BookAuthorRepository bookAuthorRepository;
    private final ElasticsearchClient elasticsearchClient;
    private final BookCategoryRepository bookCategoryRepository;
    private static final Logger USER_LOG = LoggerFactory.getLogger("user-logger");
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    @Override
    public Page<BookSearchResponseDTO> searchBooks(String type,String keyword, Pageable pageable, String userId) {
        if("weight".equalsIgnoreCase(type))
        {
            return elasticSearchRepository.searchBooksWithWeightedFields(keyword, pageable,userId);
        }
        return elasticSearchRepository.searchBooksByField(type,keyword,pageable);
    }

    //저장 후의 book 엔티티를 가져옴.
    @Override
    public void saveBook(Book book) {
        // BookRequestDto -> Book 변환
        BookDocument bookDocument = convertToBookDocument(book);

        // Elasticsearch에 저장
        try {
            elasticSearchRepository.save(bookDocument);
        } catch (Exception e) {
            USER_LOG.warn("엘라스틱 서치 레파지토리에 저장 중 오류 발생");
        }
    }

    //entity -> bookDocument
    private BookDocument convertToBookDocument(Book book) {
        // Book의 데이터를 BookDocument로 변환
        BookDocument bookDocument = new BookDocument();

        // 기본 정보 복사
        bookDocument.setId(book.getId());
        bookDocument.setTitle(book.getTitle());
        bookDocument.setRegularPrice(book.getRegularPrice());
        bookDocument.setSalePrice(book.getSalePrice());
        bookDocument.setStock(book.getStock());
        bookDocument.setSale(book.isSale());
        bookDocument.setIsbn(book.getIsbn());
        bookDocument.setContents(book.getContents());
        bookDocument.setExplanation(book.getExplanation());
        bookDocument.setPackaging(book.isPackaging());
        bookDocument.setPublishDate(book.getPublishDate().toString());
        bookDocument.setActive(book.isActive());
        //커버이미지는 db에서 얻어옴. N+1 발생하지만 save시 한번부르기때문에 1번만 발생.
        BookImage thumbnailImage = bookImageService.getThumbnail(book);
        if (thumbnailImage != null) {
            bookDocument.setCoverImage(thumbnailImage.getFilePath());
        }
        // Publisher 변환
        if (book.getPublisher() != null) {
            PublisherDocument publisherDocument = new PublisherDocument(
                    book.getPublisher().getId(),
                    book.getPublisher().getName()
            );
            bookDocument.setPublisher(publisherDocument);
        }

        // Series 변환
        if (book.getSeries() != null) {
            SeriesDocument seriesDocument = new SeriesDocument(
                    book.getSeries().getId(),
                    book.getSeries().getName()
            );
            bookDocument.setSeries(seriesDocument);
        }

        // Authors 변환
        List<AuthorDocument> authorDTOs = bookAuthorRepository.findAuthorsByBookId(book.getId());
        bookDocument.setAuthors(authorDTOs);

        // 도서 id에 해당하는 카테고리들의 id 를 반환받음.
        List<BookCategory> bookCategory = bookCategoryRepository.findBookCategoryByBookId(book.getId());
        List<Long> categoryIds = bookCategory.stream()
                .map(bc -> bc.getCategory().getId())  // 'bc'는 bookCategory 리스트의 각 요소
                .toList();
        //위에서 가져온 카테고리 id 에 해당하는 카테고리들을 가져옴.
        List<Category> categoryList = categoryRepository.findByIdIn(categoryIds);
        //카테고리 doc 객체로 변환
        List<CategoryDocument> categoryDocuments =
                categoryList.stream().map(category -> new CategoryDocument(category.getId(),category.getName())).toList();

        bookDocument.setCategories(categoryDocuments);

        // BookTags 변환
        if (book.getBookTags() != null && !book.getBookTags().isEmpty()) {
            List<TagDocument> tags = book.getBookTags().stream()
                    .map(bookTag -> new TagDocument(bookTag.getTag().getId(), bookTag.getTag().getName()))
                    .toList();
            bookDocument.setBookTags(tags);
        }

        // BookImages 변환
        if (book.getBookImages() != null && !book.getBookImages().isEmpty()) {
            List<BookImageDocument> bookImageDocuments = book.getBookImages().stream()
                    .map(bookImage -> new BookImageDocument(bookImage.getId(), bookImage.getFilePath(), bookImage.isThumbnail()))
                    .toList();
            bookDocument.setBookImages(bookImageDocuments);
        }

        return bookDocument;
    }
}
