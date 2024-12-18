package store.aurora.key;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "key.manager")
public class KeyProperties {
    private String url;
    private String path;
    private String appKey;
    private String password;
}
