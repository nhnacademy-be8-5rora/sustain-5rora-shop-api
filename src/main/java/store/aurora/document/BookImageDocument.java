package store.aurora.document;

import org.springframework.data.elasticsearch.annotations.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "5rora")
public class BookImageDocument {

    private Long id;

    private String filePath;

    private boolean isThumbnail;
}
