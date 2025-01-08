package store.aurora.order.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import store.aurora.order.entity.Wrap;
import store.aurora.order.exception.exception404.WrapNotFoundException;
import store.aurora.order.repository.WrapRepository;
import store.aurora.order.service.impl.WrapServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class WrapServiceTest {

    private WrapService wrapService;
    private WrapRepository wrapRepository;

    @BeforeEach
    void setUp() {
        wrapRepository = Mockito.mock(WrapRepository.class);
        wrapService = new WrapServiceImpl(wrapRepository);
    }

    @Test
    void isExist() {
        Mockito.when(wrapRepository.existsById(1L)).thenReturn(true);

        Assertions.assertAll(
                () -> assertTrue(wrapService.isExist(1L)),
                () -> assertFalse(wrapService.isExist(100L))
        );

    }

    @Test
    void createWrap() {
        Wrap wrap = new Wrap();
        wrap.setAmount(1000);
        wrap.setName("포장지");
        Mockito.when(wrapRepository.save(wrap)).thenReturn(wrap);

        wrapService.createWrap(wrap);

        Mockito.verify(wrapRepository, Mockito.times(1)).save(wrap);
    }

    @Test
    void createWrapThrowException() {
        Wrap wrap = new Wrap();
        wrap.setAmount(1000);

        Assertions.assertAll(
                () -> assertThrows(IllegalArgumentException.class, ()->wrapService.createWrap(null)),
                () -> assertThrows(IllegalArgumentException.class, ()->wrapService.createWrap(wrap))
        );

    }

    @Test
    void getWrap() {
        Wrap wrap = new Wrap();
        wrap.setAmount(1000);
        wrap.setName("포장지");

        Mockito.when(wrapRepository.existsById(1L)).thenReturn(true);
        Mockito.when(wrapRepository.getReferenceById(1L)).thenReturn(wrap);

        assertEquals(wrap, wrapService.getWrap(1L));
    }

    @Test
    void getWraps() {
        Wrap wrap1 = new Wrap();
        wrap1.setAmount(1000);
        wrap1.setName("포장지");

        Wrap wrap2 = new Wrap();
        wrap1.setAmount(1000);
        wrap1.setName("포장지");

        Mockito.when(wrapRepository.findAll()).thenReturn(List.of(wrap1, wrap2));

        assertEquals(List.of(wrap1, wrap2), wrapService.getWraps());
    }

    @Test
    void deleteByWrapId() {
        Mockito.when(wrapRepository.existsById(1L)).thenReturn(true);
        Mockito.doNothing().when(wrapRepository).deleteById(1L);

        wrapService.deleteByWrapId(1L);

        Mockito.verify(wrapRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void deleteByWrapIdThrowException() {

        Mockito.when(wrapRepository.existsById(100L)).thenReturn(false);

        Assertions.assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> wrapService.deleteByWrapId(null)),
                () -> assertThrows(WrapNotFoundException.class, () -> wrapService.deleteByWrapId(100L))
        );
    }
}