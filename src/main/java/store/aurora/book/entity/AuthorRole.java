package store.aurora.book.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "author_roles")
public class AuthorRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        AUTHOR,
        EDITOR;

        @JsonCreator
        public static Role fromString(String str) {
            for (Role value : Role.values()) {
                if (value.name().equalsIgnoreCase(str)) {
                    return value;
                }
            }
            throw new IllegalArgumentException("Invalid role: " + str);
        }

        @JsonValue
        public String toJson() {
            return this.name().toLowerCase();
        }
    }
}
