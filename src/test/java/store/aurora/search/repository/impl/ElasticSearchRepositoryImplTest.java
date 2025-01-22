package store.aurora.search.repository.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import store.aurora.book.dto.SearchBookDTO;
import store.aurora.book.repository.book.BookViewRepository;
import store.aurora.book.repository.like.LikeRepository;
import store.aurora.document.AuthorDocument;
import store.aurora.document.CategoryDocument;
import store.aurora.document.PublisherDocument;
import store.aurora.document.TagDocument;
import store.aurora.review.repository.ReviewRepository;
import store.aurora.review.service.ReviewService;
import store.aurora.search.dto.BookSearchResponseDTO;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ElasticSearchRepositoryImplTest {

    @Mock
    private ElasticsearchClient elasticsearchClient;

    @InjectMocks
    private ElasticSearchRepositoryImpl elasticSearchRepository;

    @Mock
    private BookViewRepository bookViewRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewService reviewService;

    @Mock
    private LikeRepository likeRepository;

    @BeforeEach
    void setUp() {
    }
    @DisplayName("특정 검색기준을 통하여 값을 가져옴. (제목)")
    @Test
    void searchBooksByField_Success() throws IOException {
        // Arrange
        String field = "title";
        String keyword = "test";
        Pageable pageable = PageRequest.of(0, 10);

        // Create mock SearchBookDTO objects
        SearchBookDTO book1 = new SearchBookDTO();
        book1.setId(1L);
        book1.setTitle("Effective Java");
        book1.setRegularPrice(45000);
        book1.setSalePrice(40000);
        book1.setStock(50);
        book1.setSale(true);
        book1.setIsbn("978-0134685991");
        book1.setContents("A comprehensive guide to Java programming.");
        book1.setExplanation("Detailed explanations of Java best practices.");
        book1.setPublishDate("2023-01-01");
        book1.setCoverImage("https://example.com/effective-java.jpg");

        book1.setAuthors(List.of(new AuthorDocument(1L, "Author Name", "Role"))); // Initialize authors
        book1.setCategories(List.of(new CategoryDocument(1L, "Category"))); // Initialize categories
        book1.setBookTags(List.of(new TagDocument(1L, "Tag"))); // Initialize tags
        book1.setPublisher(new PublisherDocument(1L, "Addison-Wesley")); // Set publisher

        // Create another book with mock data
        SearchBookDTO book2 = new SearchBookDTO();
        book2.setId(2L);
        book2.setTitle("Clean Code");
        book2.setRegularPrice(40000);
        book2.setSalePrice(35000);
        book2.setStock(30);
        book2.setSale(true);
        book2.setIsbn("978-0132350884");
        book2.setContents("A handbook of Agile software craftsmanship.");
        book2.setExplanation("Guidelines for writing clean and maintainable code.");
        book2.setPublishDate("2019-05-15");
        book2.setCoverImage("https://example.com/clean-code.jpg");

        // Populate authors
        AuthorDocument author2 = new AuthorDocument(2L, "Robert C. Martin", "Author");
        book2.setAuthors(List.of(author2));

        // Populate publisher
        PublisherDocument publisher2 = new PublisherDocument(2L, "Prentice Hall");
        book2.setPublisher(publisher2);

        // Populate categories
        CategoryDocument category2 = new CategoryDocument(2L, "Software Engineering");
        book2.setCategories(List.of(category2));

        // Populate tags
        TagDocument tag3 = new TagDocument(3L, "Clean Code");
        TagDocument tag4 = new TagDocument(4L, "Software Craftsmanship");
        book2.setBookTags(List.of(tag3, tag4));

        // Mock hits and total hits
        List<Hit<SearchBookDTO>> hits = List.of(
                new Hit.Builder<SearchBookDTO>()
                        .index("test-index") // Set the index
                        .id("1") // Set the id
                        .source(book1) // Set the source
                        .build(),
                new Hit.Builder<SearchBookDTO>()
                        .index("test-index") // Set the index
                        .id("2") // Set the id
                        .source(book2) // Set the source
                        .build()
        );

        // Create a mock SearchResponse
        SearchResponse<SearchBookDTO> mockResponse = new SearchResponse.Builder<SearchBookDTO>()
                .took(100) // Add the 'took' property
                .timedOut(false) // Add the 'timedOut' property
                .shards(s -> s
                        .total(1) // Total number of shards
                        .successful(1) // Number of successful shards
                        .skipped(0) // Number of skipped shards
                        .failed(0) // Number of failed shards
                )
                .hits(h -> h
                        .total(t -> t
                                .value(2L) // Total hits
                                .relation(TotalHitsRelation.Eq) // Exact hits
                        )
                        .hits(hits) // The actual search hits
                )
                .build();

        // Mock ElasticsearchClient's search behavior
        when(elasticsearchClient.search(any(SearchRequest.class), eq(SearchBookDTO.class)))
                .thenReturn(mockResponse);

        // Act
        Page<BookSearchResponseDTO> result = elasticSearchRepository.searchBooksByField(field, keyword, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());

        // Verify the first book
        BookSearchResponseDTO firstBook = result.getContent().get(0);
        assertEquals("Effective Java", firstBook.getTitle());
        assertEquals("Addison-Wesley", firstBook.getPublisherName());

        // Verify the second book
        BookSearchResponseDTO secondBook = result.getContent().get(1);
        assertEquals("Clean Code", secondBook.getTitle());
        assertEquals("Prentice Hall", secondBook.getPublisherName());

        // Verify that the search was executed twice
        verify(elasticsearchClient, times(2)).search(any(SearchRequest.class), eq(SearchBookDTO.class));
    }

    @DisplayName("layout에서 검색 시제목,작가이름,카테고리이름,태그이름 전반적인 가중치를 통하여 가져옴.")
    @Test
    void searchBooksWithWeightedFields_Success() throws IOException {
        // Arrange
        String keyword = "test";
        Pageable pageable = PageRequest.of(0, 10);
        String userId = "user123";

        // Create a mock SearchBookDTO object for the first book
        SearchBookDTO book1 = new SearchBookDTO();
        book1.setId(1L);
        book1.setTitle("Weighted Book 1");
        book1.setRegularPrice(50000);
        book1.setSalePrice(45000);
        book1.setStock(10);
        book1.setSale(true);
        book1.setIsbn("978-1111111111");
        book1.setContents("This is a test book about weight.");
        book1.setExplanation("A comprehensive guide to testing weighted searches.");
        book1.setPublishDate("2023-05-01");
        book1.setCoverImage("https://example.com/weighted-book-1.jpg");

        // Populate authors for the first book
        AuthorDocument author1 = new AuthorDocument(1L, "John Doe", "Author");
        book1.setAuthors(List.of(author1));

        // Populate categories for the first book
        CategoryDocument category1 = new CategoryDocument(1L, "Education");
        book1.setCategories(List.of(category1));

        // Populate tags for the first book
        TagDocument tag1 = new TagDocument(1L, "Testing");
        TagDocument tag2 = new TagDocument(2L, "Weight");
        book1.setBookTags(List.of(tag1, tag2));

        // Create a mock SearchBookDTO object for the second book
        SearchBookDTO book2 = new SearchBookDTO();
        book2.setId(2L);
        book2.setTitle("Weighted Book 2"); // Common keyword "Weighted" in the title
        book2.setRegularPrice(60000);
        book2.setSalePrice(50000);
        book2.setStock(5);
        book2.setSale(true);
        book2.setIsbn("978-2222222222");
        book2.setContents("Another test book about weight.");
        book2.setExplanation("This book discusses weighted searching techniques.");
        book2.setPublishDate("2023-06-01");
        book2.setCoverImage("https://example.com/weighted-book-2.jpg");

        // Populate authors for the second book
        AuthorDocument author2 = new AuthorDocument(2L, "Jane Doe", "Author");
        book2.setAuthors(List.of(author2));

        // Populate categories for the second book
        CategoryDocument category2 = new CategoryDocument(2L, "Programming");
        book2.setCategories(List.of(category2));

        // Populate tags for the second book
        TagDocument tag3 = new TagDocument(3L, "Search");
        TagDocument tag4 = new TagDocument(4L, "Elasticsearch");
        book2.setBookTags(List.of(tag3, tag4));

        // Mock hits and total hits
        List<Hit<SearchBookDTO>> hits = List.of(
                new Hit.Builder<SearchBookDTO>()
                        .index("test-index")
                        .id("1")
                        .source(book1)
                        .build(),
                new Hit.Builder<SearchBookDTO>()
                        .index("test-index")
                        .id("2")
                        .source(book2)
                        .build()
        );

        // Create a mock SearchResponse
        SearchResponse<SearchBookDTO> mockResponse = new SearchResponse.Builder<SearchBookDTO>()
                .took(100)
                .timedOut(false)
                .shards(s -> s
                        .total(1)
                        .successful(1)
                        .skipped(0)
                        .failed(0)
                )
                .hits(h -> h
                        .total(t -> t
                                .value(2L) // Mock 2 total hits
                                .relation(TotalHitsRelation.Eq)
                        )
                        .hits(hits)
                )
                .build();

        // Mock ElasticsearchClient's search behavior
        when(elasticsearchClient.search(any(SearchRequest.class), eq(SearchBookDTO.class)))
                .thenReturn(mockResponse);

        // Act
        Page<BookSearchResponseDTO> result = elasticSearchRepository.searchBooksWithWeightedFields(keyword, pageable, userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements()); // Ensure both books are returned

        // Verify the first book
        BookSearchResponseDTO firstBook = result.getContent().get(0);
        assertEquals("Weighted Book 1", firstBook.getTitle());
        assertEquals("John Doe", firstBook.getAuthors().get(0).getName());
        assertEquals(1, firstBook.getCategoryIdList().get(0));

        // Verify the second book
        BookSearchResponseDTO secondBook = result.getContent().get(1);
        assertEquals("Weighted Book 2", secondBook.getTitle());
        assertEquals("Jane Doe", secondBook.getAuthors().get(0).getName());
        assertEquals(2, secondBook.getCategoryIdList().get(0));

        // Verify that the search was called twice.
        verify(elasticsearchClient, times(2)).search(any(SearchRequest.class), eq(SearchBookDTO.class));
    }


}
