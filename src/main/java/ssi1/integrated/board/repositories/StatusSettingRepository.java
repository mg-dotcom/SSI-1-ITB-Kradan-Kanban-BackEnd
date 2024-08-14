package ssi1.integrated.board.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssi1.integrated.board.entities.StatusSetting;

@Repository
public interface StatusSettingRepository extends JpaRepository<StatusSetting, Integer> {

}