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
		List<Meeting> meetings = meetingRepository.findAll();

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

		if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
			return false;
		}

		LocalTime start = startTime.toLocalTime();

		LocalTime end = endTime.toLocalTime();

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

		System.out.println("=== 進入 create() 方法 ===");
		
		if (!isValidTime(meetingRequest.getStartTime(), meetingRequest.getEndTime())) {
			return Optional.empty();
		}

		if (isOverlapping(meetingRequest.getRoomId(), meetingRequest.getStartTime(), meetingRequest.getEndTime())) {
			return Optional.empty();
		}

		Optional<Employee> optEmployee = employeeRepository.findById(meetingRequest.getEmployeeId());
		Optional<Room> optRoom = roomRepository.findById(meetingRequest.getRoomId());
		Optional<Status> optStatus = statusRepository.findById(6); // 預設「待審核」

		if (optEmployee.isEmpty() || optRoom.isEmpty() || optStatus.isEmpty()) {
			return Optional.empty();
		}
		
//		if (optEmployee.isEmpty()) {
//		    System.out.println("❌ 員工不存在：" + meetingRequest.getEmployeeId());
//		}
//		if (optRoom.isEmpty()) {
//		    System.out.println("❌ 會議室不存在：" + meetingRequest.getRoomId());
//		}
//		if (optStatus.isEmpty()) {
//		    System.out.println("❌ 找不到 status_id = 6！");
//		}


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

}
