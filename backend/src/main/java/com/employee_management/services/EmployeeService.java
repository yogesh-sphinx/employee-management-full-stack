package com.employee_management.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.employee_management.entities.Employee;
import com.employee_management.repository.EmployeeRepository;

import jakarta.transaction.Transactional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    @Transactional
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    
    public Optional<Employee> updateEmployee(Long id, Employee employeeDetails) {
        return employeeRepository.findById(id).map(employee -> {
            employee.setName(employeeDetails.getName());
            employee.setDepartment(employeeDetails.getDepartment());
            employee.setSalary(employeeDetails.getSalary());
            employee.setUpdatedAt(java.time.LocalDateTime.now());
            return employeeRepository.save(employee);
        });
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
}