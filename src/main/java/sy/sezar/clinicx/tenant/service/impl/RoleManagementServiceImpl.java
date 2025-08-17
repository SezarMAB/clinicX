package sy.sezar.clinicx.tenant.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;
import sy.sezar.clinicx.core.exception.BusinessRuleException;
import sy.sezar.clinicx.tenant.constants.TenantConstants;
import sy.sezar.clinicx.tenant.service.RoleManagementService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of RoleManagementService.
 * Manages role validation, conversion, and hierarchy operations.
 */
@Slf4j
@Service
public class RoleManagementServiceImpl implements RoleManagementService {
    
    @Override
    public void validateRoleAssignment(Set<StaffRole> currentUserRoles, Set<StaffRole> rolesToAssign) {
        if (currentUserRoles == null || currentUserRoles.isEmpty()) {
            throw new BusinessRuleException("User has no roles to validate assignment privileges");
        }
        
        if (rolesToAssign == null || rolesToAssign.isEmpty()) {
            return; // Nothing to validate
        }
        
        StaffRole highestUserRole = getHighestRole(currentUserRoles);
        
        for (StaffRole roleToAssign : rolesToAssign) {
            if (!highestUserRole.hasAuthorityOver(roleToAssign)) {
                throw new BusinessRuleException(
                    String.format("User with role %s cannot assign role %s", 
                                highestUserRole.name(), roleToAssign.name())
                );
            }
        }
    }
    
    @Override
    public boolean isAdministrator(Set<StaffRole> roles) {
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        
        return roles.stream().anyMatch(StaffRole::isAdministrative);
    }
    
    @Override
    public boolean isSuperAdmin(Set<StaffRole> roles) {
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        
        return roles.contains(StaffRole.SUPER_ADMIN);
    }
    
    @Override
    public boolean isClinical(Set<StaffRole> roles) {
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        
        return roles.stream().anyMatch(StaffRole::isClinical);
    }
    
    @Override
    public StaffRole getHighestRole(Set<StaffRole> roles) {
        if (roles == null || roles.isEmpty()) {
            return getDefaultRole();
        }
        
        return roles.stream()
            .max(Comparator.comparing(StaffRole::getHierarchyLevel))
            .orElse(getDefaultRole());
    }
    
    @Override
    public Set<StaffRole> parseRoles(List<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return Set.of(getDefaultRole());
        }
        
        Set<StaffRole> roles = new HashSet<>();
        for (String roleName : roleNames) {
            try {
                roles.add(StaffRole.valueOf(roleName.toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn(TenantConstants.WARN_UNKNOWN_ROLE, roleName);
                // Skip invalid roles rather than defaulting
            }
        }
        
        // If no valid roles were parsed, add default role
        if (roles.isEmpty()) {
            roles.add(getDefaultRole());
        }
        
        return roles;
    }
    
    @Override
    public List<String> rolesToStrings(Set<StaffRole> roles) {
        if (roles == null || roles.isEmpty()) {
            return List.of();
        }
        
        return roles.stream()
            .map(StaffRole::name)
            .sorted()
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean isValidRole(String roleName) {
        if (roleName == null || roleName.trim().isEmpty()) {
            return false;
        }
        
        try {
            StaffRole.valueOf(roleName.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    @Override
    public StaffRole getDefaultRole() {
        return StaffRole.ASSISTANT;
    }
    
    @Override
    public boolean hasAuthorityOver(StaffRole role1, StaffRole role2) {
        if (role1 == null || role2 == null) {
            return false;
        }
        
        return role1.hasAuthorityOver(role2);
    }
    
    @Override
    public Set<StaffRole> mergeRoles(Set<StaffRole> roles1, Set<StaffRole> roles2) {
        Set<StaffRole> merged = new HashSet<>();
        
        if (roles1 != null) {
            merged.addAll(roles1);
        }
        
        if (roles2 != null) {
            merged.addAll(roles2);
        }
        
        // If still empty, add default role
        if (merged.isEmpty()) {
            merged.add(getDefaultRole());
        }
        
        return merged;
    }
    
    @Override
    public Set<StaffRole> filterAssignableRoles(Set<StaffRole> userRoles, Set<StaffRole> requestedRoles) {
        if (requestedRoles == null || requestedRoles.isEmpty()) {
            return Set.of();
        }
        
        if (userRoles == null || userRoles.isEmpty()) {
            // User with no roles cannot assign any roles
            return Set.of();
        }
        
        StaffRole highestUserRole = getHighestRole(userRoles);
        
        return requestedRoles.stream()
            .filter(role -> highestUserRole.hasAuthorityOver(role))
            .collect(Collectors.toSet());
    }
    
    @Override
    public Set<StaffRole> getAssignableRoles(Set<StaffRole> userRoles) {
        if (userRoles == null || userRoles.isEmpty()) {
            return Set.of();
        }
        
        StaffRole highestUserRole = getHighestRole(userRoles);
        
        // Get all roles that this user can assign
        return Arrays.stream(StaffRole.values())
            .filter(role -> highestUserRole.hasAuthorityOver(role))
            .collect(Collectors.toSet());
    }
}