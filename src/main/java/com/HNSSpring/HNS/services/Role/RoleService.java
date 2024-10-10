package com.HNSSpring.HNS.services.Role;

import com.HNSSpring.HNS.models.Role;
import com.HNSSpring.HNS.repositories.RoleRepository;
import com.HNSSpring.HNS.services.Role.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {
    private final RoleRepository roleRepository;
    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
