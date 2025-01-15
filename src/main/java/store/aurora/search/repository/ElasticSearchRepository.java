package store.aurora.search.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import store.aurora.document.BookDocument;

public interface ElasticSearchRepository extends ElasticsearchRepository<BookDocument,Long> {
}
