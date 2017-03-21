package com.pedroaugust8.share.persistence;

import com.pedroaugust8.share.entities.impl.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
}
