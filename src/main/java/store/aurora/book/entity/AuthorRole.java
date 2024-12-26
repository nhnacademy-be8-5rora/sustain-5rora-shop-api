package store.aurora.book.entity;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 50)
    private Role role;

    public enum Role {
        AUTHOR("지은이"),
        EDITOR("엮은이"),
        TRANSLATOR("옮긴이"),
        ADAPTER("편역"),
        ILLUSTRATOR("그림"),
        SUPERVISOR("감수"),
        PLANNER("기획"),
        CHARACTER("캐릭터"),
        COMPOSER("구성"),
        MAP("지도"),
        DRAWING("삽화"),
        ILLUSTRATION("일러스트"),
        PHOTOGRAPHER("사진"),
        ORIGINAL("원작"),
        WRITER("글"),
        COMMENTATOR("해설");

        private final String koreanName;

        Role(String koreanName) {
            this.koreanName = koreanName;
        }
        public String getKoreanName() {
            return koreanName;
        }
    }
}
