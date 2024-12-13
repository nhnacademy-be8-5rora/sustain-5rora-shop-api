package store.aurora.user.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Data
public class MessagePayload {
    private String botName;
    private String text;
    private Map<String,String> attachments;     // 선택 사항

    public MessagePayload(String botName, String text) {
        this.botName = botName;
        this.text = text;
    }
}
