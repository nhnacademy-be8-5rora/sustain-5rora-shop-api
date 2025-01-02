package store.aurora.book.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.entity.Series;
import store.aurora.book.repository.SeriesRepository;
import store.aurora.book.service.SeriesService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeriesServiceImpl implements SeriesService {
    private final SeriesRepository seriesRepository;

    @Override
    public List<Series> getAllSeries() {
        return seriesRepository.findAll();
    }

    @Override
    public Series getSeriesById(Long id) {
        return seriesRepository.findById(id).orElseThrow(() -> new RuntimeException("Series not found"));
    }

    @Transactional
    @Override
    public Series createSeries(Series series) {
        return seriesRepository.save(series);
    }

    @Transactional
    @Override
    public Series updateSeries(Long id, Series updatedSeries) {
        Series existingSeries = getSeriesById(id);
        existingSeries.setName(updatedSeries.getName());
        return seriesRepository.save(existingSeries);
    }

    @Transactional
    @Override
    public void deleteSeries(Long id) {
        seriesRepository.deleteById(id);
    }

    @Transactional
    @Override
    public Series getOrCreateSeries(String name) {
        return seriesRepository.findByName(name)
                .orElseGet(() -> seriesRepository.save(new Series(name)));
    }
}
