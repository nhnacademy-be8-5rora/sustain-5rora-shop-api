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
//todo interface 분리하기
public class PublisherServiceImpl implements PublisherService {
    private final PublisherRepository publisherRepository;

    public Publisher findOrCreatePublisher(String publisherName) {
        return publisherRepository.findByName(publisherName)
                .orElseGet(() -> {
                    Publisher newPublisher = new Publisher();
                    newPublisher.setName(publisherName);
                    return publisherRepository.save(newPublisher);
                });
    }

}
