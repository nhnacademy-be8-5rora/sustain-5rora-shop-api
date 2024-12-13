package store.aurora.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import store.aurora.user.dto.MessagePayload;

@FeignClient(name = "messageSendClient", url = "https://nhnacademy.dooray.com/services")
public interface MessageSendClient {

    @PostMapping("/{serviceId}/{botId}/{botToken}")
    String sendMessage(@RequestBody MessagePayload messagePayload,
                       @PathVariable Long serviceId, @PathVariable Long botId, @PathVariable String botToken);

}
