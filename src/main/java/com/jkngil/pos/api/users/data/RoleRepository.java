package com.jkngil.pos.api.users.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity, Long>{
	RoleEntity findByName(String name);
}
