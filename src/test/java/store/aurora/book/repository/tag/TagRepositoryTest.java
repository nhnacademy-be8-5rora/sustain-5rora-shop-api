package store.aurora.book.repository.tag;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import store.aurora.book.entity.tag.Tag;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Slf4j
class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;


    // todo 에러
//    @DisplayName("태그 저장 테스트")
//    @Test
//    public void saveTagTest() {
//
//        //given
//        Tag tag = new Tag(1L, "test");
//        tagRepository.save(tag);
//
//        //when
//        Tag findTag = tagRepository.findById(1L).orElse(null);
//
//        //then
//        assertThat(findTag.getName()).isEqualTo("test");
//    }

    @DisplayName("태그 삭제 테스트")
    @Test
    public void deleteTagTest() {
        //given
        Tag tag = new Tag(1L, "test");
        tagRepository.save(tag);

        //when
        tagRepository.deleteById(1L);
        Tag findTag = tagRepository.findById(1L).orElse(null);

        //then
        assertThat(findTag).isNull();
    }
}
