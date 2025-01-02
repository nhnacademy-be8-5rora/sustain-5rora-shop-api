package store.aurora.book.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.entity.Publisher;
import store.aurora.book.repository.PublisherRepository;
import store.aurora.book.service.PublisherService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublisherServiceImpl implements PublisherService {
    private final PublisherRepository publisherRepository;

    @Override
    public List<Publisher> getAllPublishers() {
        return publisherRepository.findAll();
    }

    @Override
    public Publisher getPublisherById(Long id) {
        return publisherRepository.findById(id).orElseThrow(() -> new RuntimeException("Publisher not found"));
    }

    @Transactional
    @Override
    public Publisher createPublisher(Publisher publisher) {
        return publisherRepository.save(publisher);
    }

    @Transactional
    @Override
    public Publisher updatePublisher(Long id, Publisher updatedPublisher) {
        Publisher existingPublisher = getPublisherById(id);
        existingPublisher.setName(updatedPublisher.getName());
        return publisherRepository.save(existingPublisher);
    }

    @Transactional
    @Override
    public void deletePublisher(Long id) {
        publisherRepository.deleteById(id);
    }

    @Transactional
    @Override
    public Publisher getOrCreatePublisher(String name) {
        return publisherRepository.findByName(name)
                .orElseGet(() -> publisherRepository.save(new Publisher(name)));
    }
}
