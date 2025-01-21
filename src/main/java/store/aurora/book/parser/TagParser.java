package store.aurora.book.parser;

import org.springframework.stereotype.Component;
import store.aurora.book.entity.tag.BookTag;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TagParser {

    public List<String> parseTags(String tags) {
        if (tags == null || tags.trim().isEmpty()) {
            return Collections.emptyList(); // null 또는 빈 문자열이면 빈 리스트 반환
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim) // 공백 제거
                .filter(tag -> !tag.isEmpty()) // 빈 태그 제거
                .toList();
    }

    public String formatTags(List<BookTag> bookTags) {
        return bookTags.stream()
                .map(bookTag -> bookTag.getTag().getName())
                .collect(Collectors.joining(", "));
    }
}