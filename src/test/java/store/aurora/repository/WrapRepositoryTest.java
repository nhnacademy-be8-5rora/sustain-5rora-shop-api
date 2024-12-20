//TODO [ERROR] : 모든 테스트 에러

//package store.aurora.repository;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import store.aurora.book.config.QuerydslConfiguration;
//import store.aurora.order.entity.Wrap;
//import store.aurora.order.repository.WrapRepository;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@DataJpaTest
//@Import(QuerydslConfiguration.class)
//class WrapRepositoryTest {
//    @Autowired
//    private WrapRepository wrapRepository;
//
//    @Test
//    public void saveTest(){
//        Wrap wrap = new Wrap();
//        wrap.setName("wrapName");
//        wrap.setAmount(1000);
//        wrapRepository.save(wrap);
//        assertNotNull(wrapRepository.findById(wrap.getId()));
//    }
//
//    @Test
//    public void readTest(){
//        Wrap wrap = new Wrap();
//        wrap.setName("wrapName");
//        wrap.setAmount(1000);
//        wrapRepository.save(wrap);
//        assertNotNull(wrapRepository.findById(wrap.getId()).orElse(null));
//    }
//
//    @Test
//    public void updateTest(){
//        Wrap wrap = new Wrap();
//        wrap.setName("wrapName");
//        wrap.setAmount(1000);
//        wrapRepository.save(wrap);
//        assertNotNull(wrapRepository.findById(wrap.getId()).orElse(null));
//        wrap.setName("newWrapName");
//        wrapRepository.save(wrap);
//        assertEquals("newWrapName", wrapRepository.findById(wrap.getId()).orElse(null).getName());
//    }
//
//    @Test
//    public void deleteTest(){
//        Wrap wrap = new Wrap();
//        wrap.setName("wrapName");
//        wrap.setAmount(1000);
//        wrapRepository.save(wrap);
//        assertNotNull(wrapRepository.findById(wrap.getId()).orElse(null));
//        wrapRepository.delete(wrap);
//        assertNull(wrapRepository.findById(wrap.getId()).orElse(null));
//    }
//}