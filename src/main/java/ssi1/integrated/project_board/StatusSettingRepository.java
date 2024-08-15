<<<<<<<< HEAD:src/main/java/ssi1/integrated/board/repositories/StatusSettingRepository.java
package ssi1.integrated.board.repositories;
========
package ssi1.integrated.project_board;
>>>>>>>> pbi15-bi:src/main/java/ssi1/integrated/project_board/StatusSettingRepository.java


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
<<<<<<<< HEAD:src/main/java/ssi1/integrated/board/repositories/StatusSettingRepository.java
import ssi1.integrated.board.entities.StatusSetting;
========
import ssi1.integrated.project_board.StatusSetting;
>>>>>>>> pbi15-bi:src/main/java/ssi1/integrated/project_board/StatusSettingRepository.java

@Repository
public interface StatusSettingRepository extends JpaRepository<StatusSetting, Integer> {

}