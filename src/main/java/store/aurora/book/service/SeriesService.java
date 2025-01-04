package store.aurora.book.service;


import org.springframework.transaction.annotation.Transactional;
import store.aurora.book.entity.Series;

import java.util.List;

public interface SeriesService {
    List<Series> getAllSeries();
    Series getSeriesById(Long id);
    Series createSeries(Series series);
    Series updateSeries(Long id, Series updatedSeries);
    void deleteSeries(Long id);
    Series getOrCreateSeries(String name);
}
