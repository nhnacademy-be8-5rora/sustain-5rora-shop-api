package store.aurora.book.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
