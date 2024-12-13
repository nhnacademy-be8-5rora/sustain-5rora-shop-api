package store.aurora.book.dto.tag;

import jakarta.validation.constraints.NotNull;

public record TagRequestDto(@NotNull String name) {
}
