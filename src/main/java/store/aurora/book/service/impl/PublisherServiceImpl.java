package store.aurora.book.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.entity.Publisher;
import store.aurora.book.repository.PublisherRepository;
import store.aurora.book.service.PublisherService;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PublisherServiceImpl implements PublisherService {
    private final PublisherRepository publisherRepository;

    public List<Publisher> getAllPublishers() {
        return publisherRepository.findAll();
    }

    public Publisher getPublisherById(Long id) {
        return publisherRepository.findById(id).orElseThrow(() -> new RuntimeException("Publisher not found"));
    }

    public Publisher createPublisher(Publisher publisher) {
        return publisherRepository.save(publisher);
    }

    public Publisher updatePublisher(Long id, Publisher updatedPublisher) {
        Publisher existingPublisher = getPublisherById(id);
        existingPublisher.setName(updatedPublisher.getName());
        return publisherRepository.save(existingPublisher);
    }

    public void deletePublisher(Long id) {
        publisherRepository.deleteById(id);
    }

}
