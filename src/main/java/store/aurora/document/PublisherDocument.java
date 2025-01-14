package store.aurora.document;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "5rora")
public class PublisherDocument {
    private Long id;

    private String name;

}