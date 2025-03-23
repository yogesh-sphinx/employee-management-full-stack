package com.employee_management.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityNotFoundException;

@Aspect
@Component
public class EntityValidationAspect {
    private final ApplicationContext context;

    public EntityValidationAspect(ApplicationContext context) {
        this.context = context;
    }

    @Around("execution(* com.employee_management.services.*.*(Long, ..))")
    public Object validateEntity(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (args.length == 0 || !(args[0] instanceof Long id)) {
            throw new IllegalArgumentException("The first argument must be an entity ID (Long)");
        }

        String serviceName = joinPoint.getTarget().getClass().getSimpleName();
        if (serviceName.isEmpty()) {
            throw new IllegalStateException("Invalid service class name: " + serviceName);
        }
        String repositoryBeanName = Character.toLowerCase(serviceName.charAt(0)) + serviceName.substring(1).replace("Service", "Repository");

        JpaRepository<?, Long> repository = (JpaRepository<?, Long>) context.getBean(repositoryBeanName);

        if (repository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Entity with ID " + id + " not found.");
        }

        return joinPoint.proceed();
    }
}