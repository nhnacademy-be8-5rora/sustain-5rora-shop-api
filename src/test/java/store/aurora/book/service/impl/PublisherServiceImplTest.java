package store.aurora.book.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import store.aurora.book.entity.Publisher;
import store.aurora.book.repository.PublisherRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PublisherServiceImplTest {

    @Mock
    private PublisherRepository publisherRepository;

    @InjectMocks
    private PublisherServiceImpl publisherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllPublishers() {
        List<Publisher> mockPublishers = List.of(new Publisher(1L, "Publisher1"), new Publisher(2L, "Publisher2"));
        when(publisherRepository.findAll()).thenReturn(mockPublishers);

        List<Publisher> result = publisherService.getAllPublishers();

        assertEquals(2, result.size());
        verify(publisherRepository, times(1)).findAll();
    }

    @Test
    void testGetPublisherById() {
        Publisher mockPublisher = new Publisher(1L, "Publisher1");
        when(publisherRepository.findById(1L)).thenReturn(Optional.of(mockPublisher));

        Publisher result = publisherService.getPublisherById(1L);

        assertNotNull(result);
        assertEquals("Publisher1", result.getName());
        verify(publisherRepository, times(1)).findById(1L);
    }

    @Test
    void testCreatePublisher() {
        Publisher mockPublisher = new Publisher(null, "New Publisher");
        Publisher savedPublisher = new Publisher(1L, "New Publisher");
        when(publisherRepository.save(mockPublisher)).thenReturn(savedPublisher);

        Publisher result = publisherService.createPublisher(mockPublisher);

        assertNotNull(result);
        assertEquals("New Publisher", result.getName());
        verify(publisherRepository, times(1)).save(mockPublisher);
    }

    @Test
    void testUpdatePublisher() {
        Publisher existingPublisher = new Publisher(1L, "Old Publisher");
        Publisher updatedPublisher = new Publisher(null, "Updated Publisher");
        when(publisherRepository.findById(1L)).thenReturn(Optional.of(existingPublisher));
        when(publisherRepository.save(existingPublisher)).thenReturn(existingPublisher);

        Publisher result = publisherService.updatePublisher(1L, updatedPublisher);

        assertNotNull(result);
        assertEquals("Updated Publisher", result.getName());
        verify(publisherRepository, times(1)).findById(1L);
        verify(publisherRepository, times(1)).save(existingPublisher);
    }

    @Test
    void testDeletePublisher() {
        doNothing().when(publisherRepository).deleteById(1L);

        publisherService.deletePublisher(1L);

        verify(publisherRepository, times(1)).deleteById(1L);
    }
}