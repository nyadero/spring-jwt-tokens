package com.bronyst.springjwtroles.repository;

import com.bronyst.springjwtroles.entities.ERole;
import com.bronyst.springjwtroles.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRole(ERole role);
}
