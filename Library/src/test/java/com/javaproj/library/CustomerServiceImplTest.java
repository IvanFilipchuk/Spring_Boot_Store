package com.javaproj.library;

import com.javaproj.library.dto.CustomerDto;
import com.javaproj.library.model.Customer;
import com.javaproj.library.model.Role;
import com.javaproj.library.repository.CustomerRepository;
import com.javaproj.library.repository.RoleRepository;
import com.javaproj.library.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void saveCustomer() {
        // Arrange
        CustomerDto customerDto = new CustomerDto();
        customerDto.setFirstName("John");
        customerDto.setLastName("Doe");
        customerDto.setUsername("john.doe");
        customerDto.setPassword("password");

        when(roleRepository.findByName("CUSTOMER")).thenReturn(mock(Role.class));
        when(customerRepository.save(any(Customer.class))).thenReturn(mock(Customer.class));

        // Act
        Customer savedCustomer = customerService.save(customerDto);

        // Assert
        assertNotNull(savedCustomer);
        verify(roleRepository, times(1)).findByName("CUSTOMER");
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void findByUsername() {
        // Arrange
        String username = "john.doe";
        when(customerRepository.findByUsername(username)).thenReturn(mock(Customer.class));

        // Act
        Customer foundCustomer = customerService.findByUsername(username);

        // Assert
        assertNotNull(foundCustomer);
        verify(customerRepository, times(1)).findByUsername(username);
    }

    @Test
    void getCustomer() {
        // Arrange
        String username = "john.doe";
        Customer customer = new Customer();
        customer.setUsername(username);
        when(customerRepository.findByUsername(username)).thenReturn(customer);

        // Act
        CustomerDto customerDto = customerService.getCustomer(username);

        // Assert
        assertNotNull(customerDto);
        assertEquals(username, customerDto.getUsername());
        verify(customerRepository, times(1)).findByUsername(username);
    }

    @Test
    void changePassword() {
        // Arrange
        CustomerDto customerDto = new CustomerDto();
        customerDto.setUsername("john.doe");
        customerDto.setPassword("newPassword");
        Customer existingCustomer = new Customer();
        existingCustomer.setUsername("john.doe");
        existingCustomer.setPassword("oldPassword");

        when(customerRepository.findByUsername(customerDto.getUsername())).thenReturn(existingCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(mock(Customer.class));

        // Act
        Customer updatedCustomer = customerService.changePass(customerDto);

        // Assert
        assertNotNull(updatedCustomer);
        assertNotEquals("oldPassword", updatedCustomer.getPassword());
        verify(customerRepository, times(1)).findByUsername(customerDto.getUsername());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

}