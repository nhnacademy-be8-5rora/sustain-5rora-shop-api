package store.aurora.book.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.entity.Series;
import store.aurora.book.repository.SeriesRepository;
import store.aurora.book.service.SeriesService;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SeriesServiceImpl implements SeriesService {
    private final SeriesRepository seriesRepository;

    public List<Series> getAllSeries() {
        return seriesRepository.findAll();
    }

    public Series getSeriesById(Long id) {
        return seriesRepository.findById(id).orElseThrow(() -> new RuntimeException("Series not found"));
    }

    public Series createSeries(Series series) {
        return seriesRepository.save(series);
    }

    public Series updateSeries(Long id, Series updatedSeries) {
        Series existingSeries = getSeriesById(id);
        existingSeries.setName(updatedSeries.getName());
        return seriesRepository.save(existingSeries);
    }

    public void deleteSeries(Long id) {
        seriesRepository.deleteById(id);
    }
}
