package store.aurora.book.dto;

import lombok.Data;
import store.aurora.book.entity.AuthorRole;

@Data
public class AuthorDTO {
    private String name;
    private String role;
    // 기본 생성자
    public AuthorDTO() {}
    public AuthorDTO(String name, String role) {
        this.name = name;
        this.role = role;
    }

    // Getters and setters
}

