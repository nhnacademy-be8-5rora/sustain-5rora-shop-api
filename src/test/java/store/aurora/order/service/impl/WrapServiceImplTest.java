package store.aurora.order.service.impl;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import store.aurora.order.entity.Wrap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class WrapServiceImplTest {
    @Autowired
    private WrapServiceImpl wrapService;

    @Test
    void isExist() {
        Wrap wrap = new Wrap();
        wrap.setAmount(1000);
        wrap.setName("포장지");
        wrapService.createWrap(wrap);

        assertTrue(wrapService.isExist(wrap.getId()));

        assertFalse(wrapService.isExist(100L));
    }

    @Test
    void createWrap() {
        Wrap wrap = new Wrap();
        wrap.setAmount(1000);
        wrap.setName("포장지");
        wrapService.createWrap(wrap);

        assertNotNull(wrap.getId());
    }
    @Test
    void createWrapThrowException() {
        assertThrows(IllegalArgumentException.class, ()->wrapService.createWrap(null));

        Wrap wrap = new Wrap();
        wrap.setAmount(1000);
        assertThrows(IllegalArgumentException.class, ()->wrapService.createWrap(wrap));
    }

    @Test
    void getWrap() {
        Wrap wrap = new Wrap();
        wrap.setAmount(1000);
        wrap.setName("포장지");
        wrapService.createWrap(wrap);

        assertNotNull(wrapService.getWrap(wrap.getId()));
    }

    @Test
    void getWraps() {
        Wrap wrap = new Wrap();
        wrap.setAmount(1000);
        wrap.setName("포장지");
        wrapService.createWrap(wrap);

        Wrap wrap1 = new Wrap();
        wrap1.setAmount(1000);
        wrap1.setName("포장지");
        wrapService.createWrap(wrap1);

        assertNotNull(wrapService.getWraps());
        assertEquals(2, wrapService.getWraps().size());
    }

    @Test
    void updateWrap() {
        Wrap wrap = new Wrap();
        wrap.setAmount(1000);
        wrap.setName("포장지");
        wrapService.createWrap(wrap);

        wrap.setAmount(2000);
        wrapService.updateWrap(wrap);

        Wrap getWrap = wrapService.getWrap(wrap.getId());
        assertEquals(2000, getWrap.getAmount());
    }

    @Test
    void updateWrapThrowExceptionsInputNull() {
        assertThrows(IllegalArgumentException.class, ()->wrapService.updateWrap(null));
    }

    @Test
    void updateWrapThrowExceptionNotCreated(){
        Wrap wrap = new Wrap();
        wrap.setAmount(1000);
        wrap.setName("포장지");
        assertThrows(IllegalArgumentException.class, ()->wrapService.updateWrap(wrap));
        wrapService.createWrap(wrap);
    }

    @Test
    void updateWrapThrowExceptionNameNull(){
        Wrap wrap = new Wrap();
        wrap.setAmount(1000);
        wrap.setName("포장지");
        wrapService.createWrap(wrap);

        wrap.setName(null);
        assertThrows(IllegalArgumentException.class, ()->wrapService.updateWrap(wrap));
    }

    @Test
    void updateWrapThrowExceptionNotExist() {
        Wrap wrap = new Wrap();
        wrap.setAmount(1000);
        wrap.setName("포장지");
        wrapService.createWrap(wrap);

        assertDoesNotThrow(()->wrapService.updateWrap(wrap));
        wrapService.deleteByWrapId(wrap.getId());
        assertThrows(IllegalArgumentException.class, ()->wrapService.updateWrap(wrap));
    }

    @Test
    void deleteByWrapId() {
        Wrap wrap = new Wrap();
        wrap.setAmount(1000);
        wrap.setName("포장지");
        wrapService.createWrap(wrap);

        wrapService.deleteByWrapId(wrap.getId());
        assertFalse(wrapService.isExist(wrap.getId()));
    }

    @Test
    void deleteByWrapIdThrowException() {
        assertThrows(IllegalArgumentException.class, ()->wrapService.deleteByWrapId(null));
        assertThrows(IllegalArgumentException.class, ()->wrapService.deleteByWrapId(100L));
    }
}