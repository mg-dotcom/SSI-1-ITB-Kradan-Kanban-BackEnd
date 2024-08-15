package ssi1.integrated.project_board;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssi1.integrated.project_board.StatusSetting;

@Repository
public interface StatusSettingRepository extends JpaRepository<StatusSetting, Integer> {

}