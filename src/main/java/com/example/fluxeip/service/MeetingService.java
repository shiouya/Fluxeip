package com.example.fluxeip.service;

import com.example.fluxeip.dto.MeetingRequest;
import com.example.fluxeip.dto.MeetingResponse;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.Meeting;
import com.example.fluxeip.model.Room;
import com.example.fluxeip.model.Status;
import com.example.fluxeip.repository.EmployeeRepository;
import com.example.fluxeip.repository.MeetingRepository;
import com.example.fluxeip.repository.RoomRepository;
import com.example.fluxeip.repository.StatusRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MeetingService {

	@Autowired
	private MeetingRepository meetingRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	private StatusRepository statusRepository;

	// 查詢有會議
	public List<MeetingResponse> findAll() {
	    
	    List<Meeting> meetings = meetingRepository.findAllByOrderByCreatedAtDesc();

	    
	    if (meetings.isEmpty()) {
	        return new ArrayList<>();
	    }

	    
	    List<MeetingResponse> meetingResponses = new ArrayList<>();
	    for (Meeting meeting : meetings) {
	        meetingResponses.add(new MeetingResponse(meeting));
	    }

	    return meetingResponses;
	}


	// 用Id查會議
	public Optional<MeetingResponse> findById(Integer id) {
		Optional<Meeting> optMeeting = meetingRepository.findById(id);

		if (id == null) {
			return Optional.empty();
		}

		if (optMeeting.isPresent()) {

			Meeting meeting = optMeeting.get();

			return Optional.of(new MeetingResponse(meeting));

		} else {
			return Optional.empty();
		}
	}

	// 用RoomId查會議
	public List<MeetingResponse> findByRoomId(Integer roomId) {

		List<Meeting> meetings = meetingRepository.findByRoomId(roomId);

		if (meetings.isEmpty()) {
			return new ArrayList<>();
		}

		List<MeetingResponse> meetingResponses = new ArrayList<>();

		for (Meeting meeting : meetings) {
			meetingResponses.add(new MeetingResponse(meeting));
		}
		return meetingResponses;
	}

	// 是否為有效時間
	private boolean isValidTime(LocalDateTime startTime, LocalDateTime endTime) {
		if (startTime == null || endTime == null || startTime.isAfter(endTime)) {
			return false;
		}

		DayOfWeek day = startTime.getDayOfWeek();
		LocalTime start = startTime.toLocalTime();
		LocalTime end = endTime.toLocalTime();

		if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
			return false;
		}

		if (!startTime.toLocalDate().equals(endTime.toLocalDate())) {
			System.out.println("❌ 會議時間不能跨天");
			return false;
		}

		if (start.isBefore(LocalTime.of(8, 0)) || end.isAfter(LocalTime.of(18, 0))) {
			return false;
		} else {
			return true;
		}
	}

	// 檢查是否有重疊的會議(新贈用)
	private boolean isOverlapping(Integer roomId, LocalDateTime startTime, LocalDateTime endTime) {
		return meetingRepository.existsByRoomIdAndStartTimeBeforeAndEndTimeAfter(roomId, endTime, startTime);
	}

	// 檢查是否有重疊的會議（更新用 不含自己）
	private boolean isOverlappingExceptSelf(Integer meetingId, Integer roomId, LocalDateTime startTime,
			LocalDateTime endTime) {
		return meetingRepository.existsByRoomIdAndStartTimeBeforeAndEndTimeAfterAndIdNot(roomId, startTime, startTime,
				meetingId);
	}

	// 新增
	public Optional<MeetingResponse> create(MeetingRequest meetingRequest) {

		if (!isValidTime(meetingRequest.getStartTime(), meetingRequest.getEndTime())) {
			return Optional.of(new MeetingResponse("會議時間不合法（跨天或不在上班時間內）"));
		}


		if (isOverlapping(meetingRequest.getRoomId(), meetingRequest.getStartTime(), meetingRequest.getEndTime())) {
			 return Optional.of(new MeetingResponse("會議室時段衝突"));
		}

		Optional<Employee> optEmployee = employeeRepository.findById(meetingRequest.getEmployeeId());
		Optional<Room> optRoom = roomRepository.findById(meetingRequest.getRoomId());
		Optional<Status> optStatus = statusRepository.findById(4); 

		if (optEmployee.isEmpty() || optRoom.isEmpty() || optStatus.isEmpty()) {
			 return Optional.of(new MeetingResponse("員工、會議室或狀態不存在"));
		}

		Meeting meeting = new Meeting();

		meeting.setTitle(meetingRequest.getTitle());
		meeting.setNotes(meetingRequest.getNotes());
		meeting.setStartTime(meetingRequest.getStartTime());
		meeting.setEndTime(meetingRequest.getEndTime());
		meeting.setEmployee(optEmployee.get());
		meeting.setRoom(optRoom.get());
		meeting.setStatus(optStatus.get());

		meetingRepository.save(meeting);

		return Optional.of(new MeetingResponse(meeting));
	}

	// 更新
	public Optional<MeetingResponse> update(Integer id, MeetingRequest meetingRequest) {

		Optional<Meeting> optMeetings = meetingRepository.findById(id);

		if (optMeetings.isEmpty()) {
			return Optional.empty();
		}

		Meeting meeting = optMeetings.get();

		if (!isValidTime(meetingRequest.getStartTime(), meetingRequest.getEndTime())) {
			return Optional.empty();
		}

		if (isOverlappingExceptSelf(id, meetingRequest.getRoomId(), meetingRequest.getStartTime(),
				meetingRequest.getEndTime())) {
			return Optional.empty();
		}

		Optional<Employee> optEmployee = employeeRepository.findById(meetingRequest.getEmployeeId());
		Optional<Room> optRoom = roomRepository.findById(meetingRequest.getRoomId());

		if (optEmployee.isEmpty() || optRoom.isEmpty()) {
			return Optional.empty();
		}

		meeting.setTitle(meetingRequest.getTitle());
		meeting.setNotes(meetingRequest.getNotes());
		meeting.setStartTime(meetingRequest.getStartTime());
		meeting.setEndTime(meetingRequest.getEndTime());
		meeting.setEmployee(optEmployee.get());
		meeting.setRoom(optRoom.get());

		meetingRepository.save(meeting);

		return Optional.of(new MeetingResponse(meeting));

	}

	// 刪除
	public boolean delete(Integer id) {

		if (id == null) {
			return false;
		}

		Optional<Meeting> optionalMeeting = meetingRepository.findById(id);
		if (optionalMeeting.isEmpty()) {
			return false;
		}
		meetingRepository.deleteById(id);
		return true;
	}
	
	// 取得使用者的職位
	public List<MeetingResponse> findByUser(Integer employeeId) {
	    
	    Optional<Employee> optEmployee = employeeRepository.findById(employeeId);
	    if (optEmployee.isEmpty()) {
	        return new ArrayList<>(); 
	    }

	    // 取得員工物件，並查詢職位 ID
	    Employee employee = optEmployee.get();
	    Integer positionId = employee.getPosition().getPositionId(); 

	    List<Meeting> meetings; // 會議列表

	    // 根據職位決定查詢範圍
	    if (positionId == 1 || positionId == 2) {
	        
	        meetings = meetingRepository.findAll();
	    } else {
	        
	        meetings = meetingRepository.findByEmployeeEmployeeId(employeeId);
	    }

	    
	    List<MeetingResponse> meetingResponses = new ArrayList<>();
	    for (Meeting meeting : meetings) {
	        meetingResponses.add(new MeetingResponse(meeting));
	    }

	    return meetingResponses;
	}

	
	
	
	
	

}
