package store.aurora.book.dto;

import lombok.Data;
import store.aurora.book.entity.AuthorRole;

@Data
public class AuthorDTO {
    private String name;
    private String role;

    public AuthorDTO(String name, String role) {
        this.name = name;
        this.role = role;
    }

    // Getters and setters
}

