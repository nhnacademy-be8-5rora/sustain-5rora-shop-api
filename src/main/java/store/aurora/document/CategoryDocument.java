package store.aurora.document;

import org.springframework.data.elasticsearch.annotations.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "5rora")
public class CategoryDocument {

    private Long id;

    private String name;

    private List<CategoryDocument> children = new ArrayList<>();

}
