package store.aurora.order.process.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.common.setting.service.SettingService;
import store.aurora.order.process.service.DeliveryFeeService;

@Service
@RequiredArgsConstructor
public class DeliveryFeeServiceImpl implements DeliveryFeeService {
    private final SettingService settingService;

    /*
     * todo 배송비 로직 수정
     *   매 주문마다 배송비를 setting에서 불러오는 것이 아닌 캐싱하여 사용하도록 수정
     *      2-1. 특정 시간마다 배송비를 캐싱하고, 배송비를 가져올 때 캐싱된 값을 사용
     *      2-2. 배송비 관련 정보가 변경되었을 때 캐싱된 값을 삭제하고, 새로운 값을 캐싱
     */
    @Override
    public int getDeliveryFee(int totalAmount) {
        if(totalAmount < 0){
            throw new IllegalArgumentException("totalAmount is less than 0");
        }

        if (totalAmount >= settingService.getMinAmount()) {
            return 0;
        } else {
            return settingService.getDeliveryFee();
        }
    }
}
