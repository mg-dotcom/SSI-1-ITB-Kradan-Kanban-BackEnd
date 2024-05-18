package ssi1.integrated.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssi1.integrated.entities.StatusSetting;

import java.util.Optional;

@Repository
public interface StatusSettingRepository extends JpaRepository<StatusSetting, Integer> {

}