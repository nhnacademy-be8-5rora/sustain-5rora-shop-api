package store.aurora.book.service;

import store.aurora.book.entity.Series;

public interface SeriesService {
    Series findOrCreateSeries(String seriesName);
}
