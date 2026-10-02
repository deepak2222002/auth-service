package web.minda.project.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.transaction.Transactional;
import web.minda.project.entity.DepartmentMaster;
import web.minda.project.entity.LoginMaster;
import web.minda.project.entity.PlantMaster;
import web.minda.project.entity.RoleMaster;

public interface LoginMasterRepository extends CrudRepository<LoginMaster, Long> {


	
	public boolean existsByEmail(@Param("email") String email);
	
	
	@Query("select l_m from LoginMaster l_m where l_m.email = :email ")
	public LoginMaster findByEmail(@PathVariable("email") String email);

	
	
}
