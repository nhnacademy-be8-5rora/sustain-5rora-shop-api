package store.aurora.common.setting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.aurora.common.setting.SettingTable;

public interface SettingRepository extends JpaRepository<SettingTable, String> {
}
