package store.aurora.book.service.publisher.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import store.aurora.book.dto.publisher.PublisherRequestDto;
import store.aurora.book.dto.publisher.PublisherResponseDto;
import store.aurora.book.entity.Publisher;
import store.aurora.book.exception.publisher.PublisherAlreadyExistsException;
import store.aurora.book.exception.publisher.PublisherLinkedToBooksException;
import store.aurora.book.exception.publisher.PublisherNotFoundException;
import store.aurora.book.repository.book.BookRepository;
import store.aurora.book.repository.publisher.PublisherRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PublisherServiceImplTest {

    @InjectMocks
    private PublisherServiceImpl publisherService;

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private BookRepository bookRepository;

    private Publisher testPublisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testPublisher = new Publisher(1L, "테스트 출판사");
    }

    @Test
    @DisplayName("모든 출판사 목록을 페이지네이션으로 조회")
    void testGetAllPublishers() {
        // Given
        Pageable pageable = mock(Pageable.class);
        List<Publisher> publishers = List.of(testPublisher);
        when(publisherRepository.findAllByOrderById(pageable)).thenReturn(new PageImpl<>(publishers));

        // When
        Page<PublisherResponseDto> result = publisherService.getAllPublishers(pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getName()).isEqualTo("테스트 출판사");
    }

    @Test
    @DisplayName("ID로 출판사 조회")
    void testGetPublisherById() {
        // Given
        when(publisherRepository.findById(1L)).thenReturn(Optional.of(testPublisher));

        // When
        PublisherResponseDto response = publisherService.getPublisherById(1L);

        // Then
        assertThat(response.getName()).isEqualTo("테스트 출판사");
    }

    @Test
    @DisplayName("존재하지 않는 출판사 조회 시 예외 발생")
    void testGetPublisherById_NotFound() {
        // Given
        when(publisherRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> publisherService.getPublisherById(1L))
                .isInstanceOf(PublisherNotFoundException.class);
    }

    @Test
    @DisplayName("새로운 출판사 추가")
    void testCreatePublisher() {
        // Given
        PublisherRequestDto request = new PublisherRequestDto("새 출판사");
        when(publisherRepository.existsByName(request.getName())).thenReturn(false);

        // When
        publisherService.createPublisher(request);

        // Then
        verify(publisherRepository, times(1)).save(any(Publisher.class));
    }

    @Test
    @DisplayName("중복된 출판사 추가 시 예외 발생")
    void testCreatePublisher_Duplicate() {
        // Given
        PublisherRequestDto request = new PublisherRequestDto("테스트 출판사");
        when(publisherRepository.existsByName(request.getName())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> publisherService.createPublisher(request))
                .isInstanceOf(PublisherAlreadyExistsException.class);
    }

    @Test
    @DisplayName("출판사 정보 수정")
    void testUpdatePublisher() {
        // Given
        PublisherRequestDto updateRequest = new PublisherRequestDto("수정된 출판사");
        when(publisherRepository.findById(1L)).thenReturn(Optional.of(testPublisher));
        when(publisherRepository.existsByName(updateRequest.getName())).thenReturn(false);

        // When
        publisherService.updatePublisher(1L, updateRequest);

        // Then
        assertThat(testPublisher.getName()).isEqualTo("수정된 출판사");
    }

    @Test
    @DisplayName("출판사 삭제")
    void testDeletePublisher() {
        // Given
        when(publisherRepository.existsById(1L)).thenReturn(true);
        when(bookRepository.existsByPublisherId(1L)).thenReturn(false);

        // When
        publisherService.deletePublisher(1L);

        // Then
        verify(publisherRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 출판사 삭제 시 예외 발생")
    void testDeletePublisher_NotFound() {
        // Given
        when(publisherRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> publisherService.deletePublisher(1L))
                .isInstanceOf(PublisherNotFoundException.class);
    }

    @Test
    @DisplayName("연결된 책이 있는 출판사 삭제 시 예외 발생")
    void testDeletePublisher_LinkedToBooks() {
        // Given
        when(publisherRepository.existsById(1L)).thenReturn(true);
        when(bookRepository.existsByPublisherId(1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> publisherService.deletePublisher(1L))
                .isInstanceOf(PublisherLinkedToBooksException.class);
    }

    @Test
    @DisplayName("존재하는 출판사 조회 시 새로운 객체를 생성하지 않고 반환해야 한다")
    void testGetOrCreatePublisher_Existing() {
        // Given
        when(publisherRepository.findByName("테스트 출판사")).thenReturn(Optional.of(testPublisher));

        // When
        Publisher result = publisherService.getOrCreatePublisher("테스트 출판사");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("테스트 출판사");
        verify(publisherRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 출판사 조회 시 새로 생성하고 저장해야 한다")
    void testGetOrCreatePublisher_New() {
        // Given
        when(publisherRepository.findByName("새 출판사")).thenReturn(Optional.empty());
        when(publisherRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Publisher result = publisherService.getOrCreatePublisher("새 출판사");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("새 출판사");
        verify(publisherRepository, times(1)).save(any(Publisher.class));
    }
}