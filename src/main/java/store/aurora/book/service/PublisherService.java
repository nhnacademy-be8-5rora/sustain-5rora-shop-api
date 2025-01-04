package store.aurora.book.service;

import store.aurora.book.entity.Publisher;

import java.util.List;

public interface PublisherService {
    List<Publisher> getAllPublishers();
    Publisher getPublisherById(Long id);
    Publisher createPublisher(Publisher publisher);
    Publisher updatePublisher(Long id, Publisher updatedPublisher);
    void deletePublisher(Long id);
    Publisher getOrCreatePublisher(String name);
}
