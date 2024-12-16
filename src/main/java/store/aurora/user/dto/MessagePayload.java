package store.aurora.user.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Data
public class MessagePayload {
    private String botName;
    private String text;

    public MessagePayload(String botName, String text) {
        this.botName = botName;
        this.text = text;
    }
}
