package store.aurora.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.entity.Publisher;
import store.aurora.book.repository.PublisherRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PublisherService {
    private final PublisherRepository publisherRepository;

    public Publisher findOrCreatePublisher(String publisherName) {
        return publisherRepository.findByName(publisherName)
                .orElseGet(() -> {
                    Publisher newPublisher = new Publisher();
                    newPublisher.setName(publisherName);
                    return publisherRepository.save(newPublisher);
                });
    }

    public List<Publisher> findAll() {
        return publisherRepository.findAll();
    }


    public void deleteById(Long id) {
        publisherRepository.deleteById(id);
    }
}
