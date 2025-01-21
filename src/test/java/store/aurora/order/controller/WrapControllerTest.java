package store.aurora.order.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import store.aurora.order.dto.WrapResponseDTO;
import store.aurora.order.entity.Wrap;
import store.aurora.order.service.WrapService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WrapControllerTest {

    private WrapService wrapService;
    private WrapController wrapController;

    @BeforeEach
    void setUp() {
        wrapService = Mockito.mock(WrapService.class);
        wrapController = new WrapController(wrapService);
    }

    @Test
    void getWrapListTest() {
        Wrap wrap = new Wrap();
        wrap.setId(1L);
        wrap.setName("test");
        wrap.setAmount(500);
        List<Wrap> expect = List.of(wrap);
        Mockito.when(wrapService.getWraps()).thenReturn(expect);

        List<WrapResponseDTO> wrapDtos = wrapController.getWrapList().getBody();
        assertEquals(expect.size() + 1, wrapDtos.size());
    }

    @Test
    void createWrapTest() {
        WrapResponseDTO wrapDto = WrapResponseDTO.builder().name("test").amount(100).build();
        Mockito.doNothing().when(wrapService).createWrap(Mockito.any(Wrap.class));

        wrapController.createWrap(wrapDto);

        Mockito.verify(wrapService, Mockito.times(1)).createWrap(Mockito.any(Wrap.class));
    }

    @Test
    void updateWrapTest() {
        WrapResponseDTO wrapDto = WrapResponseDTO.builder().name("test").amount(100).build();
        Mockito.doNothing().when(wrapService).updateWrap(Mockito.any(Wrap.class));

        wrapController.updateWrap(wrapDto);

        Mockito.verify(wrapService, Mockito.times(1)).updateWrap(Mockito.any(Wrap.class));
    }

    @Test
    void deleteWrapTest() {
        Mockito.doNothing().when(wrapService).deleteByWrapId(1L);

        wrapController.deleteWrap(1L);

        Mockito.verify(wrapService, Mockito.times(1)).deleteByWrapId(1L);
    }
}