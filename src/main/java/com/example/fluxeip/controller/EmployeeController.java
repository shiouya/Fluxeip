package com.example.fluxeip.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.fluxeip.dto.EmployeeDetailResponse;
import com.example.fluxeip.dto.EmployeePageRequest;
import com.example.fluxeip.dto.EmployeePageResponse;
import com.example.fluxeip.dto.EmployeeUpdateRequest;
import com.example.fluxeip.dto.LoginResponse;
import com.example.fluxeip.model.Department;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.EmployeeDetail;
import com.example.fluxeip.model.Position;
import com.example.fluxeip.model.Roles;
import com.example.fluxeip.model.Status;
import com.example.fluxeip.repository.EmployeeDetailRepository;
import com.example.fluxeip.repository.EmployeeRepository;
import com.example.fluxeip.repository.RolesRepository;
import com.example.fluxeip.service.DepartmentService;
import com.example.fluxeip.service.EmployeeApprovalFlowService;
import com.example.fluxeip.service.EmployeeDetailService;
import com.example.fluxeip.service.EmployeeService;
import com.example.fluxeip.service.PositionService;
import com.example.fluxeip.service.StatusService;

@CrossOrigin
@RestController
public class EmployeeController {
	
	@Autowired
	private PositionService posSer;

	@Autowired
	private DepartmentService depSer;

	@Autowired
    private EmployeeService employeeService;
	
	@Autowired
	private EmployeeRepository empRep;

	@Autowired
	private EmployeeDetailRepository empDetRep;

	@Autowired
	private EmployeeDetailService empDetSer;

	@Autowired
	private StatusService staSer;

	@Autowired
	private RolesRepository rolesRep;
	
	@Autowired
	private EmployeeApprovalFlowService employeeApprovalFlowService;

	@PostMapping("/employee/find")
	public EmployeePageResponse getEmployeesPage(@RequestBody EmployeePageRequest page) {
		EmployeePageResponse empPage = new EmployeePageResponse();
		Status status = staSer.findById(1);
		if ((page.getDepartment() != null && page.getDepartment().length() > 0)
				&& (page.getPosition() == null || page.getPosition().length() == 0)) {
			Department department = depSer.findByName(page.getDepartment());
			long countByDepartment = empRep.countByDepartmentAndStatus(department,status);
			empPage.setCount(countByDepartment);
			Page<Employee> employeesByDepartment = employeeService.getEmployeesByDepartment(department, status,
					page.getCurrent(), page.getRows());
			empPage.setLists(employeesByDepartment);
			return empPage;
		} else if ((page.getDepartment() == null || page.getDepartment().length() == 0)
				&& (page.getPosition() != null && page.getPosition().length() > 0)) {
			Position position = posSer.findByName(page.getPosition());
			long countByPosition = empRep.countByPositionAndStatus(position,status);
			empPage.setCount(countByPosition);
			Page<Employee> employeesByPosition = employeeService.getEmployeesByPosition(position, status,
					page.getCurrent(),
					page.getRows());
			empPage.setLists(employeesByPosition);
			return empPage;
		} else if (page.getDepartment() == null || page.getDepartment().length() == 0 || page.getPosition() == null
				|| page.getPosition().length() == 0) {
			long allcount = empRep.countByStatus(status);
			empPage.setCount(allcount);
			Page<Employee> allemployees = employeeService.getEmployees(status, page.getCurrent(), page.getRows());
			empPage.setLists(allemployees);
			return empPage;
		}else {
			Department department = depSer.findByName(page.getDepartment());
			Position position = posSer.findByName(page.getPosition());
			long countByDepartmentAndPosition = empRep.countByDepartmentAndPositionAndStatus(department, position,status);
			empPage.setCount(countByDepartmentAndPosition);
			Page<Employee> employeesByDepartmentAndPosition = employeeService
					.getEmployeesByDepartmentAndPosition(department, position, status, page.getCurrent(), page.getRows());
			empPage.setLists(employeesByDepartmentAndPosition);
			return empPage;
		}
    }

	@GetMapping("/employee/detail/{id}")
	public EmployeeDetailResponse getEmployeeDetail(@PathVariable Integer id) {
		EmployeeDetailResponse empDetRes = new EmployeeDetailResponse();
		Employee employee = employeeService.find(id);
		EmployeeDetail empDet = empDetSer.empDetByIdFind(id);

		empDetRes.setEmployeeId(id);
		empDetRes.setEmployeeName(employee.getEmployeeName());
		empDetRes.setDepartment(employee.getDepartment().getDepartmentName());
		empDetRes.setPosition(employee.getPosition().getPositionName());
		empDetRes.setHireDate(employee.getHireDate());
		empDetRes.setStatus(employee.getStatus().getStatusName());

		String photo = null;
		if (empDet.getEmployeePhoto() == null) {
			photo = "data:image/png;base64,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";
		} else {
			photo = "data:image/png;base64," + Base64.getEncoder().encodeToString(empDet.getEmployeePhoto());
		}
		empDetRes.setEmployeePhoto(photo);

		empDetRes.setEnergencyPhone(empDet.getEnergencyPhone());
		empDetRes.setGender(empDet.getGender());
		empDetRes.setBirthday(empDet.getBirthday());
		empDetRes.setAddress(empDet.getAddress());
		empDetRes.setEmergencyContact(empDet.getEmergencyContact());
		empDetRes.setEmail(empDet.getEmail());
		empDetRes.setPhone(empDet.getPhone());
		empDetRes.setIdentityCard(empDet.getIdentityCard());

		return empDetRes;
	}

	@PostMapping("/employee/detail/update")
	public boolean employeeDetailUpdate(@RequestParam Map<String, String> formData,
			@RequestParam(value = "photoFile", required = false) MultipartFile photoFile) {
		LoginResponse response = new LoginResponse();

		Integer employeeId = Integer.valueOf(formData.get("employeeId"));
		String email = formData.get("email");
		String phone = formData.get("phone");
		String address = formData.get("address");
		String emergencyContact = formData.get("emergencyContact");
		String emergencyPhone = formData.get("energencyPhone");
		EmployeeDetail empDet = empDetSer.empDetByIdFind(employeeId);
		if (empDetSer.isEmailExist(email) && !empDet.getEmail().equals(email)) {
			return false;
		} else if (empDetSer.isPhoneExist(phone) && !empDet.getPhone().equals(phone)) {
			return false;
		}
		if (photoFile != null && !photoFile.isEmpty()) {
			try {
				byte[] fileData = photoFile.getBytes();
				empDet.setEmployeePhoto(fileData);
				empDet.setAddress(address);
				empDet.setEmail(email);
				empDet.setEmergencyContact(emergencyContact);
				empDet.setEnergencyPhone(emergencyPhone);
				empDet.setPhone(phone);
				empDetRep.save(empDet);
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		empDet.setAddress(address);
		empDet.setEmail(email);
		empDet.setEmergencyContact(emergencyContact);
		empDet.setEnergencyPhone(emergencyPhone);
		empDet.setPhone(phone);
		empDetRep.save(empDet);
		return true;
	}

	@PostMapping("/employee/update")
	public boolean employeeUpdate(@RequestBody EmployeeUpdateRequest entity) {
		Integer employeeId = entity.getEmployeeId();
		Employee employee = employeeService.find(employeeId);
		String employeeName = entity.getEmployeeName();
		String departmentName = entity.getDepartment();
		String positionName = entity.getPosition();
		
	    // 如果職位變更，才執行刪除流程的邏輯
	    if (!employee.getPosition().getPositionName().equals(positionName)) {
	    	System.out.println("123");
	        try {
	            employeeApprovalFlowService.deleteAllEmployeeApprovalFlowsByEmployeeId(employeeId);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	    }
	    
		Roles roles1 = rolesRep.findByRoleName("最高管理員");
		Roles roles2 = rolesRep.findByRoleName("次等管理員");
		Roles roles3 = rolesRep.findByRoleName("行政主管");
		Roles roles4 = rolesRep.findByRoleName("人資主管");
		Roles roles5 = rolesRep.findByRoleName("業務主管");
		Roles roles6 = rolesRep.findByRoleName("技術主管");
		Roles roles7 = rolesRep.findByRoleName("員工");
		employee.getRoles().removeFirst();
		if (departmentName.equals("行政部") && positionName.equals("經理")) {
			employee.getRoles().add(roles3);
		} else if (departmentName.equals("人資部") && positionName.equals("經理")) {
			employee.getRoles().add(roles4);
		} else if (departmentName.equals("業務部") && positionName.equals("經理")) {
			employee.getRoles().add(roles5);
		} else if (departmentName.equals("技術部") && positionName.equals("經理")) {
			employee.getRoles().add(roles6);
		} else if (positionName.equals("組長") || positionName.equals("員工")) {
			employee.getRoles().add(roles7);
		} else if (departmentName.equals("總經理部") && positionName.equals("老闆")) {
			employee.getRoles().add(roles1);
		} else if (departmentName.equals("總經理部") && positionName.equals("總經理")) {
			employee.getRoles().add(roles2);
		}
		Department department = depSer.findByName(departmentName);
		Position position = posSer.findByName(positionName);
		Status status = staSer.findByName(entity.getStatus());
		employee.setDepartment(department);
		employee.setEmployeeName(employeeName);
		employee.setPosition(position);
		employee.setStatus(status);
		System.out.println(employee.getRoles() + "11111111111111111111111111111111111111");
		empRep.save(employee);
		return true;
	}

	@GetMapping("/employee/find/department/{dep}")
	public List<Employee> employeeFindDepartment(@PathVariable String dep) {
		Department department = depSer.findByName(dep);
		List<Employee> employeeFindByDepartment = employeeService.employeeFindByDepartment(department);

		return employeeFindByDepartment;
	}

	@GetMapping("/employee/find/department/emp/{id}")
	public List<Employee> employeeFindDepartmentByEmp(@PathVariable Integer id) {
		Optional<Employee> emp = empRep.findById(id);
		Employee employee = emp.get();
		Department department = depSer.findByName(employee.getDepartment().getDepartmentName());
		List<Employee> employeeFindByDepartment = employeeService.employeeFindByDepartment(department);

		return employeeFindByDepartment;
	}

	
	//利用部門和職位雙重查詢員工
	@PostMapping("/employee/search")
	public ResponseEntity<Page<Employee>> searchEmployees(
	        @RequestBody EmployeePageRequest request,
	        @RequestParam(defaultValue = "0") int page,  // 設定預設值，避免未傳入
	        @RequestParam(defaultValue = "10") int size) { // 預設一頁10筆
	    Department department = request.getDepartment() != null ? depSer.findByName(request.getDepartment()) : null;
	    Position position = request.getPosition() != null ? posSer.findByName(request.getPosition()) : null;
	    Status status = staSer.findByStatusNameAndStatusType("在職", "員工狀態");

	    // 手動建立 Pageable
	    Pageable pageable = PageRequest.of(page, size, Sort.by("employeeId").ascending());

	    Page<Employee> employees = employeeService.getEmployeesByDepartmentAndPosition(department, position, status, pageable);
	    if (employees.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(employees);
        }
	}
}
