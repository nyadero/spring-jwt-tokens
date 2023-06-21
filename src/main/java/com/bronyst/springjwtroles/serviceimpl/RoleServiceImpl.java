package com.bronyst.springjwtroles.serviceimpl;

import com.bronyst.springjwtroles.entities.Role;
import com.bronyst.springjwtroles.repository.RoleRepository;
import com.bronyst.springjwtroles.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;
    @Override
    public Role createNewRole(Role role) {
        return roleRepository.save(role);
    }
}
