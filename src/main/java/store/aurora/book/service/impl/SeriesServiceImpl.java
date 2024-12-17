package store.aurora.book.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.entity.Series;
import store.aurora.book.repository.SeriesRepository;
import store.aurora.book.service.SeriesService;

@Service
@Transactional
@RequiredArgsConstructor
//todo interface 분리하기
public class SeriesServiceImpl implements SeriesService {
    private final SeriesRepository seriesRepository;

    public Series findOrCreateSeries(String seriesName) {
        if (seriesName == null || seriesName.isEmpty()) {
            return null;
        }

        return seriesRepository.findByName(seriesName)
                .orElseGet(() -> {
                    Series newSeries = new Series();
                    newSeries.setName(seriesName);
                    return seriesRepository.save(newSeries);
                });
    }
}
