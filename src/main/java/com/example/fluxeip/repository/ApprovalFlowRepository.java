package com.example.fluxeip.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.fluxeip.model.ApprovalFlow;

public interface ApprovalFlowRepository extends JpaRepository<ApprovalFlow, Integer> {
	
	// 查詢特定 requestType 的所有流程，按 stepOrder 排序
    @Query("SELECT af FROM ApprovalFlow af WHERE af.requestType.id = :requestTypeId ORDER BY af.stepOrder ASC")
    List<ApprovalFlow> findByRequestTypeId(@Param("requestTypeId") Integer requestTypeId);

//    // 查詢特定 Position、RequestType 和 StepOrder 的流程
//    @Query("SELECT af FROM ApprovalFlow af WHERE af.position.positionId = :positionId AND af.requestType.id = :requestTypeId AND af.stepOrder = :stepOrder")
//    Optional<ApprovalFlow> findApprovalFlow(
//        @Param("positionId") Integer positionId, 
//        @Param("requestTypeId") Integer requestTypeId, 
//        @Param("stepOrder") Integer stepOrder
//    );
//    
    @Query("SELECT af FROM ApprovalFlow af WHERE af.position.positionId = :positionId AND af.requestType.id = :requestTypeId AND af.stepOrder = :stepOrder")
    List<ApprovalFlow> findApprovalFlow(
        @Param("positionId") Integer positionId, 
        @Param("requestTypeId") Integer requestTypeId, 
        @Param("stepOrder") Integer stepOrder
    );
    
    
    // 先找員工專屬的簽核流程（第一步）
    @Query("SELECT af FROM ApprovalFlow af JOIN EmployeeApprovalFlow eaf ON af.id = eaf.approvalFlow.id " +
           "WHERE eaf.employee.employeeId = :employeeId AND eaf.type.id = :requestTypeId " +
           "AND af.stepOrder = 1")
    Optional<ApprovalFlow> findFirstStepByEmployee(@Param("employeeId") Integer employeeId, 
                                                   @Param("requestTypeId") Integer requestTypeId);

	 List<ApprovalFlow> findByStepOrder(Integer stepOrder);
}


