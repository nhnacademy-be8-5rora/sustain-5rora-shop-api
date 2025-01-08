package store.aurora.order.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import store.aurora.book.config.QuerydslConfiguration;
import store.aurora.order.entity.Wrap;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(QuerydslConfiguration.class)
class WrapRepositoryTest {
    @Autowired
    private WrapRepository wrapRepository;

    @Test
    void saveTest(){
        Wrap wrap = new Wrap();
        wrap.setName("wrapName");
        wrap.setAmount(1000);
        wrapRepository.save(wrap);
        assertNotNull(wrapRepository.findById(wrap.getId()));
    }

    @Test
    void readTest(){
        Wrap wrap = new Wrap();
        wrap.setName("wrapName");
        wrap.setAmount(1000);
        wrapRepository.save(wrap);
        assertNotNull(wrapRepository.findById(wrap.getId()).orElse(null));
    }

    @Test
    void updateTest(){
        Wrap wrap = new Wrap();
        wrap.setName("wrapName");
        wrap.setAmount(1000);
        wrapRepository.save(wrap);
        assertNotNull(wrapRepository.findById(wrap.getId()).orElse(null));
        wrap.setName("newWrapName");
        wrapRepository.save(wrap);
        assertEquals("newWrapName", wrapRepository.findById(wrap.getId()).orElse(null).getName());
    }

    @Test
    void deleteTest(){
        Wrap wrap = new Wrap();
        wrap.setName("wrapName");
        wrap.setAmount(1000);
        wrapRepository.save(wrap);
        assertNotNull(wrapRepository.findById(wrap.getId()).orElse(null));
        wrapRepository.delete(wrap);
        assertNull(wrapRepository.findById(wrap.getId()).orElse(null));
    }
}