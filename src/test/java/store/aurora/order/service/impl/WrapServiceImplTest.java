package store.aurora.order.service.impl;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
    void createWrap() {
        Wrap wrap = new Wrap();
        wrap.setAmount(1000);
        wrap.setName("포장지");
        wrapService.createWrap(wrap);

        assertNotNull(wrap.getId());
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
    void deleteWrap() {
        Wrap wrap = new Wrap();
        wrap.setAmount(1000);
        wrap.setName("포장지");
        wrapService.createWrap(wrap);

        wrapService.deleteWrap(wrap);
        assertThrows(Exception.class ,()->wrapService.getWrap(wrap.getId()));
    }

    @Test
    void deleteByWrapId() {
        Wrap wrap = new Wrap();
        wrap.setAmount(1000);
        wrap.setName("포장지");
        wrapService.createWrap(wrap);

        wrapService.deleteByWrapId(wrap.getId());
        assertThrows(Exception.class ,()->wrapService.getWrap(wrap.getId()));
    }
}