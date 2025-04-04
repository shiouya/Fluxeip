package com.example.fluxeip.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fluxeip.dto.AttendeeResponse;
import com.example.fluxeip.dto.MeetingRequest;
import com.example.fluxeip.dto.MeetingResponse;
import com.example.fluxeip.model.Attendee;
import com.example.fluxeip.model.Employee;
import com.example.fluxeip.model.Meeting;
import com.example.fluxeip.model.Room;
import com.example.fluxeip.model.Status;
import com.example.fluxeip.repository.AttendeeRepository;
import com.example.fluxeip.repository.EmployeeRepository;
import com.example.fluxeip.repository.MeetingRepository;
import com.example.fluxeip.repository.RoomRepository;
import com.example.fluxeip.repository.StatusRepository;

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

	@Autowired
	private NotifyService notifyService;

	@Autowired
	private AttendeeRepository attendeeRepository;

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
	public boolean isOverlapping(Integer roomId, LocalDateTime startTime, LocalDateTime endTime) {
		List<Meeting> meetings = meetingRepository.findByRoomId(roomId);

		for (Meeting m : meetings) {
			// 只檢查 審核中 / 已審核，不要算進 未核准
			int statusId = m.getStatus().getStatusId();
			if (statusId != 4 && statusId != 5 && statusId != 6)
				continue;

			// 判斷是否時間重疊：只要有交集就是衝突
			boolean isOverlap = m.getStartTime().isBefore(endTime) && m.getEndTime().isAfter(startTime);
			if (isOverlap) {
				return true; // 有重疊
			}
		}

		return false; // 沒有重疊
	}

//	public boolean isOverlapping(Integer roomId, LocalDateTime start, LocalDateTime end) {
//	    return meetingRepository.existsByRoomAndTimeOverlapValidStatus(roomId, start, end);
//	}

//	private boolean isOverlapping(Integer roomId, LocalDateTime startTime, LocalDateTime endTime) {
//		return meetingRepository.existsByRoomIdAndStartTimeBeforeAndEndTimeAfter(roomId, endTime, startTime);
//	}

	// 檢查是否有重疊的會議（更新用 不含自己）
	private boolean isOverlappingExceptSelf(Integer meetingId, Integer roomId, LocalDateTime startTime,
			LocalDateTime endTime) {
		return meetingRepository.existsByRoomIdAndStartTimeBeforeAndEndTimeAfterAndIdNot(roomId, startTime, endTime,
				meetingId);
	}

	// 新增
	public Optional<MeetingResponse> create(MeetingRequest meetingRequest) {

		// 1. 檢查時間是否合法（起時間 < 結束時間，非週末，非上下班時間等）
		if (!isValidTime(meetingRequest.getStartTime(), meetingRequest.getEndTime())) {
			return Optional.empty();
		}

		// 2. 檢查是否有時間重疊（只考慮「審核中」「已審核」的會議）
		if (isOverlapping(meetingRequest.getRoomId(), meetingRequest.getStartTime(), meetingRequest.getEndTime())) {
			return Optional.empty(); // 有重疊，不允許新增
		}

		// 3. 查詢使用者與會議室
		Optional<Employee> optEmployee = employeeRepository.findById(meetingRequest.getEmployeeId());
		Optional<Room> optRoom = roomRepository.findById(meetingRequest.getRoomId());
		Optional<Status> optStatus = statusRepository.findById(5); // 預設「審核中」狀態

		if (optEmployee.isEmpty() || optRoom.isEmpty() || optStatus.isEmpty()) {
			return Optional.empty(); // 必要資料查不到
		}

		// 4. 建立 Meeting Entity 並存入
		Meeting meeting = new Meeting();
		meeting.setTitle(meetingRequest.getTitle());
		meeting.setNotes(meetingRequest.getNotes());
		meeting.setStartTime(meetingRequest.getStartTime());
		meeting.setEndTime(meetingRequest.getEndTime());
		meeting.setEmployee(optEmployee.get());
		meeting.setRoom(optRoom.get());
		meeting.setStatus(optStatus.get());

		Meeting savedMeeting = meetingRepository.save(meeting);

		// 5. 發送通知給審核人
		try {
			Integer approverId = 1002;
			String message = "有新的會議室預約需要您審核\n" + "（申請人：" + savedMeeting.getEmployee().getEmployeeName() + "）\n" + "（主題："
					+ savedMeeting.getTitle() + "）";

			notifyService.sendNotification(approverId, message);
		} catch (Exception e) {
			System.out.println("⚠ 發送通知失敗：" + e.getMessage());
		}

		return Optional.of(new MeetingResponse(savedMeeting));
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

	// 查會議室當天預約情況
	public List<MeetingResponse> findReserve(Integer roomId, LocalDateTime date) {

		if (roomId == null || date == null) {
			return new ArrayList<>();
		}

		LocalDateTime startOfDay = date.withHour(8).withMinute(0).withSecond(0);
		LocalDateTime endOfDay = date.withHour(18).withMinute(0).withSecond(0);

		List<Meeting> meetings = meetingRepository.findByRoomIdAndStartTimeBetween(roomId, startOfDay, endOfDay);

		if (meetings.isEmpty()) {
			return new ArrayList<>();
		}

		List<MeetingResponse> meetingResponses = new ArrayList<>();

		for (Meeting meeting : meetings) {
			meetingResponses.add(new MeetingResponse(meeting));
		}
		return meetingResponses;

	}

	// 審核
	public Optional<Meeting> approveAndReturnMeeting(Integer meetingId, Integer employeeId, boolean isApproved) {

		if (meetingId == null || employeeId == null) {
			return Optional.empty();
		}

		Optional<Meeting> optMeeting = meetingRepository.findById(meetingId);
		Optional<Employee> optEmployee = employeeRepository.findById(employeeId);

		if (optMeeting.isEmpty() || optEmployee.isEmpty()) {
			return Optional.empty();
		}

		Meeting meeting = optMeeting.get();
		Employee employee = optEmployee.get();

		if (employee.getPosition().getPositionId() != 2) {
			return Optional.empty();
		}

		if (meeting.getStatus().getStatusId() != 5) {
			return Optional.empty();
		}

		Integer newStatusId = isApproved ? 6 : 8;
		Optional<Status> optStatus = statusRepository.findById(newStatusId);
		if (optStatus.isEmpty())
			return Optional.empty();

		meeting.setStatus(optStatus.get());
		meetingRepository.save(meeting);

		return Optional.of(meeting);

	}

	public boolean addAttendees(Integer meetingId, List<Integer> employeeIds) {
	    Optional<Meeting> optMeeting = meetingRepository.findById(meetingId);

	    if (optMeeting.isEmpty() || employeeIds == null || employeeIds.isEmpty()) {
	        return false;
	    }

	    Meeting meeting = optMeeting.get();
	    Integer hostId = meeting.getEmployee().getEmployeeId();

	    // 撈出目前已存在的與會者清單
	    List<Attendee> existingAttendees = attendeeRepository.findByMeetingId(meetingId);
	    List<Integer> existingIds = existingAttendees.stream()
	            .map(a -> a.getEmployee().getEmployeeId())
	            .toList();

	    for (Integer employeeId : employeeIds) {
	        // 跳過主辦人自己
	        if (employeeId.equals(hostId)) {
	            continue;
	        }

	        // 跳過已存在的與會人員
	        if (existingIds.contains(employeeId)) {
	            continue;
	        }

	        Optional<Employee> optEmployee = employeeRepository.findById(employeeId);
	        if (optEmployee.isEmpty()) {
	            continue;
	        }

	        Attendee attendee = new Attendee();
	        attendee.setMeeting(meeting);
	        attendee.setEmployee(optEmployee.get());
	        attendee.setIsAttending(null); // 尚未回覆
	        attendee.setRespondTime(null);

	        attendeeRepository.save(attendee);

	        // 發送通知
	        String message = "你被邀請參加會議：「" + meeting.getTitle() + "」請確認是否出席。";
	        try {
	            notifyService.sendNotification(employeeId, message);
	        } catch (Exception e) {
	            System.out.println("通知發送失敗：" + e.getMessage());
	        }
	    }

	    return true;
	}

	
	// 回覆
	public boolean respondToMeeting(Integer attendeeId, Integer employeeId, boolean isAccepted) {
	    Optional<Attendee> optAttendee = attendeeRepository.findById(attendeeId);

	    if (optAttendee.isEmpty()) {
	        return false;
	    }

	    Attendee attendee = optAttendee.get();

	    if (!attendee.getEmployee().getEmployeeId().equals(employeeId)) {
	        return false;
	    }

	    attendee.setIsAttending(isAccepted);
	    attendee.setRespondTime(LocalDateTime.now());

	    attendeeRepository.save(attendee);

	    // 發送通知
	    Integer hostId = attendee.getMeeting().getEmployee().getEmployeeId();
	    String statusText = isAccepted ? "已確認參加" : "已拒絕參加";
	    String message =  attendee.getEmployee().getEmployeeName() +
	            " " + statusText + " 會議「" + attendee.getMeeting().getTitle()+ "」";

	    try {
	        notifyService.sendNotification(hostId, message);
	    } catch (Exception e) {
	        System.out.println("通知主辦人失敗：" + e.getMessage());
	    }

	    return true;
	}


	// 看自己參加過哪些會議
	public List<AttendeeResponse> findAttendeeMeetingsByEmployeeId(Integer employeeId) {

		List<Attendee> attendees = attendeeRepository.findByEmployeeEmployeeId(employeeId);

		if (attendees == null || attendees.isEmpty()) {
			return new ArrayList<>();
		}

		List<AttendeeResponse> responses = new ArrayList<>();

		for (Attendee attendee : attendees) {

			AttendeeResponse response = new AttendeeResponse(attendee);
			responses.add(response);
		}
		return responses;
	}

	//回覆統計查詢
	public List<AttendeeResponse> findAttendeesByMeetingId(Integer meetingId) {
	    // 查詢指定會議的所有與會者
	    List<Attendee> attendees = attendeeRepository.findByMeetingId(meetingId);

	    // 2如果沒有任何與會者，回傳空的 List
	    if (attendees == null || attendees.isEmpty()) {
	        return new ArrayList<>();
	    }

	    // 3準備一個回傳用的結果列表
	    List<AttendeeResponse> responseList = new ArrayList<>();

	    // 4一筆一筆轉換成 AttendeeResponse（DTO）
	    for (Attendee attendee : attendees) {
	        AttendeeResponse response = new AttendeeResponse(attendee);
	        responseList.add(response);
	    }

	    // 回傳轉換後的結果
	    return responseList;
	}


}
