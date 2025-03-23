package com.employee_management.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.employee_management.entities.Employee;
import com.employee_management.repository.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee mockEmployee;

    @BeforeEach
    void setUp() {
        mockEmployee = new Employee();
        mockEmployee.setId(1L);
        mockEmployee.setName("John Doe");
        mockEmployee.setDepartment("IT");
        mockEmployee.setSalary(50000.0);
    }

    @Test
    void getAllEmployees_ReturnsEmployeeList() {
        List<Employee> employees = Arrays.asList(mockEmployee);
        when(employeeRepository.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.getAllEmployees();

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
    }

    @Test
    void getEmployeeById_ReturnsEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));

        Optional<Employee> result = employeeService.getEmployeeById(1L);

        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
    }

    @Test
    void getEmployeeById_ReturnsEmpty_WhenEmployeeNotFound() {
        when(employeeRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Employee> result = employeeService.getEmployeeById(2L);

        assertFalse(result.isPresent());
    }

    @Test
    void createEmployee_SuccessfullyCreatesEmployee() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);

        Employee result = employeeService.createEmployee(mockEmployee);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
    }

    @Test
    void updateEmployee_SuccessfullyUpdatesEmployee() {
        Employee updatedDetails = new Employee();
        updatedDetails.setName("Jane Doe");
        updatedDetails.setDepartment("HR");
        updatedDetails.setSalary(60000.0);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);

        Optional<Employee> result = employeeService.updateEmployee(1L, updatedDetails);

        assertTrue(result.isPresent());
        assertEquals("Jane Doe", result.get().getName());
        assertEquals("HR", result.get().getDepartment());
        assertEquals(60000.0, result.get().getSalary());
    }

    @Test
    void updateEmployee_ReturnsEmpty_WhenEmployeeNotFound() {
        Employee updatedDetails = new Employee();
        updatedDetails.setName("Jane Doe");
        
        when(employeeRepository.findById(2L)).thenReturn(Optional.empty());
        
        Optional<Employee> result = employeeService.updateEmployee(2L, updatedDetails);
        
        assertFalse(result.isPresent());
    }

    @Test
    void deleteEmployee_SuccessfullyDeletesEmployee() {
        doNothing().when(employeeRepository).deleteById(1L);

        assertDoesNotThrow(() -> employeeService.deleteEmployee(1L));
        verify(employeeRepository, times(1)).deleteById(1L);
    }
}
