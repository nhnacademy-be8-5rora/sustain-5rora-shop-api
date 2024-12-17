package store.aurora.book.service;

import store.aurora.book.entity.Publisher;

public interface PublisherService {
    Publisher findOrCreatePublisher(String publisherName);

}
