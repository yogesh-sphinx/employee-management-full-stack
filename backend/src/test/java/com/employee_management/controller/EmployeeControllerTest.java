package com.employee_management.controller;

import com.employee_management.entities.Employee;
import com.employee_management.services.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setName("John Doe");
        employee.setEmail("john.doe@example.com");
    }

    @Test
    void getAllEmployees_ShouldReturnListOfEmployees() {
        when(employeeService.getAllEmployees()).thenReturn(List.of(employee));

        List<Employee> employees = employeeController.getAllEmployees();

        assertEquals(1, employees.size());
        assertEquals("John Doe", employees.get(0).getName());
    }

    @Test
    void getEmployeeById_ShouldReturnEmployee() {
        when(employeeService.getEmployeeById(1L)).thenReturn(Optional.of(employee));

        ResponseEntity<Employee> response = employeeController.getEmployeeById(1L);

        assertTrue(response.getBody() != null);
        assertEquals("John Doe", response.getBody().getName());
    }

    @Test
    void createEmployee_ShouldReturnCreatedEmployee() {
        when(employeeService.createEmployee(any(Employee.class))).thenReturn(employee);

        Employee createdEmployee = employeeController.createEmployee(employee);

        assertEquals("John Doe", createdEmployee.getName());
    }

    @Test
    void updateEmployee_ShouldReturnUpdatedEmployee() {
        when(employeeService.updateEmployee(eq(1L), any(Employee.class))).thenReturn(Optional.of(employee));

        ResponseEntity<Employee> response = employeeController.updateEmployee(1L, employee);

        assertTrue(response.getBody() != null);
        assertEquals("John Doe", response.getBody().getName());
    }

    @Test
    void deleteEmployee_ShouldReturnNoContent() {
        doNothing().when(employeeService).deleteEmployee(1L);

        ResponseEntity<Void> response = employeeController.deleteEmployee(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(employeeService, times(1)).deleteEmployee(1L);
    }
}
