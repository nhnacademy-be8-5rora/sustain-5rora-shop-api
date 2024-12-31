package store.aurora.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.common.dto.ErrorResponseDto;
import store.aurora.user.dto.UserAddressRequest;
import store.aurora.user.service.impl.UserAddressService;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class UserAddressController {
    private final UserAddressService userAddressService;

    @Operation(summary = "배송지 추가", description = "사용자가 새로운 배송지를 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "배송지가 성공적으로 추가되었습니다."),
            @ApiResponse(responseCode = "409", description = "중복된 배송지가 이미 존재합니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping
    public ResponseEntity<String> addUserAddress(
            @RequestHeader(value = "X-USER-ID") String userId,
            @RequestBody @Valid UserAddressRequest request) {
        userAddressService.addUserAddress(
                request.getReceiver(),
                request.getRoadAddress(),
                request.getAddrDetail(),
                userId
        );

        return ResponseEntity.ok("배송지가 추가되었습니다.");
    }
}