package store.aurora.book.mapper;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import store.aurora.book.dto.aladin.*;
import store.aurora.book.entity.Book;
import store.aurora.book.entity.Publisher;
import store.aurora.book.entity.Series;
import store.aurora.book.service.author.BookAuthorService;
import store.aurora.book.service.image.BookImageService;
import store.aurora.book.service.publisher.PublisherService;
import store.aurora.book.service.series.SeriesService;
import store.aurora.book.service.category.CategoryService;
import store.aurora.book.service.tag.TagService;

import java.time.LocalDate;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class BookMapperTest {

    @Mock private PublisherService publisherService;
    @Mock private SeriesService seriesService;
    @Mock private CategoryService categoryService;
    @Mock private TagService tagService;
    @Mock private BookAuthorService bookAuthorService;
    @Mock private BookImageService bookImageService;

    @InjectMocks
    private BookMapper bookMapper;

    private Book book;
    private BookRequestDto bookRequestDto;
    private AladinBookRequestDto aladinBookRequestDto;

    @BeforeEach
    void setUp() {
        Publisher publisher = new Publisher();
        publisher.setId(1L);
        publisher.setName("테스트 출판사");

        Series series = new Series();
        series.setId(1L);
        series.setName("테스트 시리즈");

        book = new Book();
        book.setId(1L);
        book.setTitle("테스트 책");
        book.setExplanation("테스트 설명");
        book.setContents("테스트 목차");
        book.setIsbn("1234567890123");
        book.setRegularPrice(20000);
        book.setSalePrice(15000);
        book.setStock(50);
        book.setSale(true);
        book.setPackaging(false);
        book.setPublishDate(LocalDate.of(2022, 1, 1));
        book.setPublisher(publisher);
        book.setSeries(series);
        bookRequestDto = new BookRequestDto();
        bookRequestDto.setTitle("테스트 책");
        bookRequestDto.setAuthor("테스트 저자");
        bookRequestDto.setDescription("테스트 설명");
        bookRequestDto.setContents("테스트 목차");
        bookRequestDto.setPublisher("테스트 출판사");
        bookRequestDto.setPubDate(LocalDate.of(2022, 1, 1));
        bookRequestDto.setIsbn("1234567890123");
        bookRequestDto.setPriceSales(15000);
        bookRequestDto.setPriceStandard(20000);
        bookRequestDto.setStock(50);
        bookRequestDto.setSale(true);
        bookRequestDto.setPackaging(false);
        bookRequestDto.setSeriesName("테스트 시리즈");
        bookRequestDto.setCategoryIds(List.of(1L, 2L));
        bookRequestDto.setTags("소설, 과학");

        aladinBookRequestDto = new AladinBookRequestDto();
        aladinBookRequestDto.setTitle("알라딘 테스트 책");
        aladinBookRequestDto.setDescription("알라딘 테스트 설명");
        aladinBookRequestDto.setContents("알라딘 테스트 목차");
        aladinBookRequestDto.setPublisher("알라딘 출판사");
        aladinBookRequestDto.setPubDate("2022-01-01");
        aladinBookRequestDto.setIsbn13("9876543210987");
        aladinBookRequestDto.setPriceSales(18000);
        aladinBookRequestDto.setPriceStandard(25000);
        aladinBookRequestDto.setStock(30);
        aladinBookRequestDto.setSale(true);
        aladinBookRequestDto.setPackaging(true);
        aladinBookRequestDto.setCategoryIds(List.of(3L, 4L));
        aladinBookRequestDto.setTags("과학, 철학");
    }

    @Test
    @DisplayName("BookRequestDto -> Book 변환 테스트")
    void toEntity_success() {
        // When
        Book book = bookMapper.toEntity(bookRequestDto);

        // Then
        assertThat(book).isNotNull();
        assertThat(book.getTitle()).isEqualTo(bookRequestDto.getTitle());
        assertThat(book.getIsbn()).isEqualTo(bookRequestDto.getIsbn());
        assertThat(book.getRegularPrice()).isEqualTo(bookRequestDto.getPriceStandard());
        assertThat(book.getSalePrice()).isEqualTo(bookRequestDto.getPriceSales());
        assertThat(book.getStock()).isEqualTo(bookRequestDto.getStock());
        assertThat(book.isSale()).isEqualTo(bookRequestDto.isSale());
        assertThat(book.isPackaging()).isEqualTo(bookRequestDto.isPackaging());
        assertThat(book.getPublishDate()).isEqualTo(bookRequestDto.getPubDate());
    }

    @Test
    @DisplayName("AladinBookRequestDto -> Book 변환 테스트")
    void aladinToEntity_success() {
        // When
        Book book = bookMapper.aladinToEntity(aladinBookRequestDto);

        // Then
        assertThat(book).isNotNull();
        assertThat(book.getTitle()).isEqualTo(aladinBookRequestDto.getTitle());
        assertThat(book.getIsbn()).isEqualTo(aladinBookRequestDto.getValidIsbn());
        assertThat(book.getRegularPrice()).isEqualTo(aladinBookRequestDto.getPriceStandard());
        assertThat(book.getSalePrice()).isEqualTo(aladinBookRequestDto.getPriceSales());
        assertThat(book.getStock()).isEqualTo(aladinBookRequestDto.getStock());
        assertThat(book.isSale()).isEqualTo(aladinBookRequestDto.isSale());
        assertThat(book.isPackaging()).isEqualTo(aladinBookRequestDto.isPackaging());
        assertThat(book.getPublishDate()).isEqualTo(LocalDate.of(2022, 1, 1));
    }

    @Test
    @DisplayName("Book -> BookResponseDto 변환 테스트")
    void toResponseDto_success() {
        // Given
        Book book = bookMapper.toEntity(bookRequestDto);

        // When
        BookResponseDto dto = bookMapper.toResponseDto(book);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getTitle()).isEqualTo(book.getTitle());
        assertThat(dto.getIsbn13()).isEqualTo(book.getIsbn());
        assertThat(dto.getPriceStandard()).isEqualTo(book.getRegularPrice());
        assertThat(dto.getPriceSales()).isEqualTo(book.getSalePrice());
        assertThat(dto.getPubDate()).isEqualTo(book.getPublishDate().toString());
        assertThat(dto.getStock()).isEqualTo(book.getStock());
        assertThat(dto.isSale()).isEqualTo(book.isSale());
        assertThat(dto.isPackaging()).isEqualTo(book.isPackaging());
    }

    @Test
    @DisplayName("Book -> BookDetailDto 변환 테스트")
    void toDetailDto_success() {
        // When
        BookDetailDto bookDetailDto = bookMapper.toDetailDto(book);

        // Then
        assertThat(bookDetailDto).isNotNull();
        assertThat(bookDetailDto.getTitle()).isEqualTo(book.getTitle());
        assertThat(bookDetailDto.getIsbn()).isEqualTo(book.getIsbn());
        assertThat(bookDetailDto.getDescription()).isEqualTo(book.getExplanation());
        assertThat(bookDetailDto.getContents()).isEqualTo(book.getContents());
        assertThat(bookDetailDto.getPriceStandard()).isEqualTo(book.getRegularPrice());
        assertThat(bookDetailDto.getPriceSales()).isEqualTo(book.getSalePrice());
        assertThat(bookDetailDto.getPubDate()).isEqualTo(book.getPublishDate());
        assertThat(bookDetailDto.getStock()).isEqualTo(book.getStock());
        assertThat(bookDetailDto.isSale()).isEqualTo(book.isSale());
        assertThat(bookDetailDto.isPackaging()).isEqualTo(book.isPackaging());
        assertThat(bookDetailDto.getPublisher()).isEqualTo(book.getPublisher().getName());
        assertThat(bookDetailDto.getSeriesName()).isEqualTo(book.getSeries().getName());
    }

    @Test
    @DisplayName("BookRequestDto -> 기존 Book 업데이트 테스트")
    void updateEntityFromDto_success() {
        // When
        bookMapper.updateEntityFromDto(book, bookRequestDto);

        // Then
        assertThat(book.getTitle()).isEqualTo(bookRequestDto.getTitle());
        assertThat(book.getExplanation()).isEqualTo(bookRequestDto.getDescription());
        assertThat(book.getContents()).isEqualTo(bookRequestDto.getContents());
        assertThat(book.getIsbn()).isEqualTo(bookRequestDto.getIsbn());
        assertThat(book.getRegularPrice()).isEqualTo(bookRequestDto.getPriceStandard());
        assertThat(book.getSalePrice()).isEqualTo(bookRequestDto.getPriceSales());
        assertThat(book.getStock()).isEqualTo(bookRequestDto.getStock());
        assertThat(book.isSale()).isEqualTo(bookRequestDto.isSale());
        assertThat(book.isPackaging()).isEqualTo(bookRequestDto.isPackaging());
        assertThat(book.getPublishDate()).isEqualTo(bookRequestDto.getPubDate());
    }
}