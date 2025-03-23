package com.employee_management.aspect;

import jakarta.persistence.EntityNotFoundException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;

import com.employee_management.entities.Employee;
import com.employee_management.repository.EmployeeRepository;
import com.employee_management.services.EmployeeService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;

class EntityValidationAspectTest {

    private EntityValidationAspect entityValidationAspect;

    @Mock
    private EmployeeRepository employeeRepository; 

    @Mock
    private ApplicationContext context;

    @InjectMocks
    private EmployeeService employeeService;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private JpaRepository<Object, Long> repository;

    private EntityValidationAspect aspect;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        entityValidationAspect = new EntityValidationAspect(context);
    }

    // @Test
    // void validateEntity_ShouldProceed_WhenEntityExists() throws Throwable {
    //     // Arrange
    //     Long employeeId = 1L;
    //     Employee employee = new Employee();
    //     when(context.getBean("employeeRepository")).thenReturn(employeeRepository);
    //     when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
    //     when(joinPoint.getArgs()).thenReturn(new Object[]{employeeId});
    //     when(joinPoint.proceed()).thenReturn(employee);

    //     // Act
    //     Object result = aspect.validateEntity(joinPoint);

    //     // Assert
    //     verify(employeeRepository).findById(employeeId);
    //     verify(joinPoint).proceed();
    // }

    // @Test
    // void validateEntity_ShouldThrowEntityNotFoundException_WhenEntityDoesNotExist() {
    //     // Arrange
    //     Long employeeId = 1L;
    //     when(context.getBean("employeeRepository")).thenReturn(employeeRepository);
    //     when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());
    //     when(joinPoint.getArgs()).thenReturn(new Object[]{employeeId});

    //     // Act & Assert
    //     assertThrows(EntityNotFoundException.class, () -> aspect.validateEntity(joinPoint));
    //     verify(employeeRepository).findById(employeeId);
    // }


    @Test
    void validateEntity_ShouldThrowIllegalArgumentException_WhenNoArgumentsProvided() throws Throwable {
        when(joinPoint.getArgs()).thenReturn(new Object[]{});

        assertThrows(IllegalArgumentException.class, () -> entityValidationAspect.validateEntity(joinPoint));
        verify(joinPoint, never()).proceed();
    }

    @Test
    void validateEntity_ShouldThrowIllegalArgumentException_WhenFirstArgumentIsNotLong() throws Throwable {
        when(joinPoint.getArgs()).thenReturn(new Object[]{"Not a Long"});

        assertThrows(IllegalArgumentException.class, () -> entityValidationAspect.validateEntity(joinPoint));
        verify(joinPoint, never()).proceed();
    }
    @Test
void validateEntity_ShouldThrowIllegalStateException_WhenServiceNameIsEmpty() {
    Long entityId = 1L;

    when(joinPoint.getArgs()).thenReturn(new Object[]{entityId});
    when(joinPoint.getTarget()).thenReturn(new Object() {
        public String getClassSimpleName() {
            return "";
        }
    });

    assertThrows(IllegalStateException.class, () -> entityValidationAspect.validateEntity(joinPoint));
}

}
