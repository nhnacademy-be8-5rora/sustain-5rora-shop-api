package store.aurora.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import store.aurora.common.dto.ErrorResponseDto;
import store.aurora.user.dto.UserAddressRequest;
import store.aurora.user.dto.UserAddressResponse;
import store.aurora.user.entity.UserAddress;
import store.aurora.user.service.impl.UserAddressService;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class UserAddressController {
    private final UserAddressService userAddressService;

    @GetMapping
    public ResponseEntity<List<UserAddressResponse>> getUserAddresses(@RequestHeader(value = "X-USER-ID") String userId) {
        // 특정 사용자의 배송지 리스트 조회
        List<UserAddress> userAddresses = userAddressService.getUserAddresses(userId);

        // UserAddress 엔터티를 DTO로 변환
        List<UserAddressResponse> response = userAddresses.stream()
                .map(UserAddressResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userAddressId}")
    public ResponseEntity<UserAddressResponse> getUserAddress(@RequestHeader(value = "X-USER-ID") String userId,
                                                              @PathVariable Long userAddressId) {
        UserAddress userAddress = userAddressService.getUserAddressByIdAndUserId(userAddressId, userId);
        return ResponseEntity.ok(UserAddressResponse.fromEntity(userAddress));
    }

    @Operation(summary = "배송지 추가", description = "사용자가 새로운 배송지를 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "배송지가 성공적으로 추가되었습니다."),
            @ApiResponse(responseCode = "409", description = "중복된 배송지가 이미 존재합니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping
    public ResponseEntity<String> addUserAddress(@RequestHeader(value = "X-USER-ID") String userId,
                                                 @RequestBody @Valid UserAddressRequest request) {
        userAddressService.addUserAddress(
                request.getReceiver(),
                request.getRoadAddress(),
                request.getAddrDetail(),
                userId
        );

        return ResponseEntity.ok("배송지가 추가되었습니다.");
    }

    @Operation(summary = "특정 배송지 수정", description = "특정 사용자의 배송지를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "배송지 수정 성공"),
            @ApiResponse(responseCode = "404", description = "해당 사용자의 배송지가 존재하지 않음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping("/{userAddressId}")
    public ResponseEntity<String> updateUserAddress(@PathVariable Long userAddressId,
                                                    @RequestHeader(value = "X-USER-ID") String userId,
                                                    @RequestBody UserAddressRequest request) {
        userAddressService.updateUserAddress(
                userAddressId,
                request.getReceiver(),
                request.getAddrDetail(),
                request.getRoadAddress(),
                userId
        );

        return ResponseEntity.ok("배송지가 수정되었습니다.");
    }

    @Operation(summary = "특정 배송지 삭제", description = "특정 사용자의 배송지를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "배송지 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "배송지가 존재하지 않음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @DeleteMapping("/{userAddressId}")
    public ResponseEntity<String> deleteUserAddress(@PathVariable Long userAddressId,
                                                    @RequestHeader(value = "X-USER-ID") String userId) {
        userAddressService.deleteUserAddress(userAddressId, userId);
        return ResponseEntity.ok("배송지가 삭제되었습니다.");
    }
}