package store.aurora.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.entity.Series;
import store.aurora.book.repository.SeriesRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class SeriesService {
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
