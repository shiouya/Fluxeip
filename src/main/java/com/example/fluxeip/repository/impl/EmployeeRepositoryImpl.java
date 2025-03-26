package com.example.fluxeip.repository.impl;

import com.example.fluxeip.model.Department;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.Position;
import com.example.fluxeip.model.Status;
import com.example.fluxeip.repository.EmployeeRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Employee> findEmployeesByDepartmentAndPosition(Department department, Position position, Status status, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
        Root<Employee> employeeRoot = cq.from(Employee.class);

        List<Predicate> predicates = new ArrayList<>();

        if (status != null) {
            predicates.add(cb.equal(employeeRoot.get("status"), status));
        }
        if (department != null) {
            predicates.add(cb.equal(employeeRoot.get("department"), department));
        }
        if (position != null) {
            predicates.add(cb.equal(employeeRoot.get("position"), position));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        // 執行查詢
        TypedQuery<Employee> query = entityManager.createQuery(cq);
        query.setFirstResult(Math.toIntExact(pageable.getOffset()));
        query.setMaxResults(pageable.getPageSize());

        // 取得總數
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Employee> countRoot = countQuery.from(Employee.class);
        List<Predicate> countPredicates = new ArrayList<>();

        if (status != null) {
            countPredicates.add(cb.equal(countRoot.get("status"), status));
        }
        if (department != null) {
            countPredicates.add(cb.equal(countRoot.get("department"), department));
        }
        if (position != null) {
            countPredicates.add(cb.equal(countRoot.get("position"), position));
        }

        countQuery.select(cb.count(countRoot)).where(countPredicates.toArray(new Predicate[0]));
        Long count = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(query.getResultList(), pageable, count);
    }
}

