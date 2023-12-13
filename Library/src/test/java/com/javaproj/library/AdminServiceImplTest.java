package com.javaproj.library;

import com.javaproj.library.dto.AdminDto;
import com.javaproj.library.model.Admin;
import com.javaproj.library.model.Role;
import com.javaproj.library.repository.AdminRepository;
import com.javaproj.library.repository.RoleRepository;
import com.javaproj.library.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class AdminServiceImplTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void saveAdmin() {
        AdminDto adminDto = new AdminDto();
        adminDto.setFirstName("John");
        adminDto.setLastName("Doe");
        adminDto.setUsername("john.doe");
        adminDto.setPassword("password");

        Role mockRole = mock(Role.class);
        when(roleRepository.findByName("ADMIN")).thenReturn(mockRole);

        Admin mockAdmin = mock(Admin.class);
        when(mockAdmin.getFirstName()).thenReturn(adminDto.getFirstName());
        when(mockAdmin.getLastName()).thenReturn(adminDto.getLastName());
        when(mockAdmin.getUsername()).thenReturn(adminDto.getUsername());
        when(mockAdmin.getPassword()).thenReturn(adminDto.getPassword());
        when(adminRepository.save(any(Admin.class))).thenReturn(mockAdmin);

        Admin savedAdmin = adminService.save(adminDto);

        assertNotNull(savedAdmin);
        assertEquals(adminDto.getFirstName(), savedAdmin.getFirstName());
        assertEquals(adminDto.getLastName(), savedAdmin.getLastName());
        assertEquals(adminDto.getUsername(), savedAdmin.getUsername());
        assertEquals(adminDto.getPassword(), savedAdmin.getPassword());
        verify(roleRepository, times(1)).findByName("ADMIN");
        verify(adminRepository, times(1)).save(any(Admin.class));
    }

}