package store.aurora.common.setting;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "settings")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class SettingTable {
    @Id
    @Column(name = "key_name", nullable = false, length = 16)
    private String key;

    @Column(name = "value", nullable = false)
    private String value;
}
