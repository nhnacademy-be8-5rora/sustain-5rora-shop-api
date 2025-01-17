package store.aurora.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import store.aurora.order.dto.WrapResponseDTO;
import store.aurora.order.entity.Wrap;
import store.aurora.order.service.WrapService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/order/wrap")
public class WrapController {

    private final WrapService wrapService;

    private static final String NONE_SELECT = "선택 안함";

    @GetMapping("/get-wrap-list")
    public ResponseEntity<List<WrapResponseDTO>> getWrapList(){
        List<WrapResponseDTO> wrapList = convertWrapEntityListToDtoList(wrapService.getWraps());
        return ResponseEntity.ok(wrapList);
    }

    private List<WrapResponseDTO> convertWrapEntityListToDtoList(List<Wrap> wraps){
        List<WrapResponseDTO> wrapResponseDTOList = new ArrayList<>();

        wrapResponseDTOList.add(
                WrapResponseDTO.builder()
                        .id(-1L)
                        .name(NONE_SELECT)
                        .amount(0)
                        .build());
        for (Wrap wrap : wraps) {
            WrapResponseDTO wrapResponseDTO = WrapResponseDTO.builder()
                    .id(wrap.getId())
                    .name(wrap.getName())
                    .amount(wrap.getAmount())
                    .build();

            wrapResponseDTOList.add(wrapResponseDTO);
        }

        return wrapResponseDTOList;
    }
}
