
package ssi1.integrated.project_board.statusSetting;
import ssi1.integrated.project_board.statusSetting.StatusSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface StatusSettingRepository extends JpaRepository<StatusSetting, Integer> {

}