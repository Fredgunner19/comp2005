package com.example.hospitalapi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class HospitalService {
    private final String ALLOCATIONS_URL = "https://web.socem.plymouth.ac.uk/COMP2005/api/allocations";
    private final String ADMISSIONS_URL = "https://web.socem.plymouth.ac.uk/COMP2005/api/admissions";
    public List<Integer> getRoomsByStaff(int staffId) {

        RestTemplate restTemplate = new RestTemplate();
        List<Map<String, Object>> allocations =
                restTemplate.getForObject(ALLOCATIONS_URL, List.class);
        return filterRoomsByStaff(allocations, staffId);
    }
    public List<Integer> filterRoomsByStaff(List<Map<String, Object>> allocations, int staffId) {
        Set<Integer> rooms = new HashSet<>();
        if (allocations != null) {
            for (Map<String, Object> allocation : allocations) {
                Number employeeNum = (Number) allocation.get("employeeID");
                Number roomNum = (Number) allocation.get("roomID");
                if (employeeNum != null && roomNum != null) {
                    int employeeId = employeeNum.intValue();
                    int roomId = roomNum.intValue();
                    if (employeeId == staffId) {
                        rooms.add(roomId);
                    }
                }
            }
        }
        return new ArrayList<>(rooms);
    }

    public List<Integer> getPatientsByRoomLast7Days(int roomId) {

        RestTemplate restTemplate = new RestTemplate();
        List<Map<String, Object>> allocations =
                restTemplate.getForObject(ALLOCATIONS_URL, List.class);
        List<Map<String, Object>> admissions =
                restTemplate.getForObject(ADMISSIONS_URL, List.class);
        Set<Integer> patients = new HashSet<>();
        if (allocations != null && admissions != null) {
            for (Map<String, Object> allocation : allocations) {
                Number roomNum = (Number) allocation.get("roomID");
                Number admissionNum = (Number) allocation.get("admissionID");
                if (roomNum != null && admissionNum != null) {
                    int currentRoomId = roomNum.intValue();
                    int admissionId = admissionNum.intValue();
                    if (currentRoomId == roomId) {
                        for (Map<String, Object> admission : admissions) {
                            Number idNum = (Number) admission.get("id");
                            Number patientNum = (Number) admission.get("patientID");
                            String dateStr = (String) admission.get("admissionDate");
                            if (idNum != null && patientNum != null && dateStr != null) {
                                int id = idNum.intValue();
                                if (id == admissionId) {
                                    java.time.LocalDateTime admissionDate =
                                            java.time.LocalDateTime.parse(dateStr);
                                    java.time.LocalDateTime sevenDaysAgo =
                                            java.time.LocalDateTime.now().minusDays(7);
                                    if (admissionDate.isAfter(sevenDaysAgo)) {
                                        patients.add(patientNum.intValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<>(patients);
    }
    public Integer getLeastUsedRoom() {

        RestTemplate restTemplate = new RestTemplate();
        List<Map<String, Object>> allocations =
                restTemplate.getForObject(ALLOCATIONS_URL, List.class);
        Map<Integer, Integer> roomCounts = new HashMap<>();
        if (allocations != null) {
            for (Map<String, Object> allocation : allocations) {
                Number roomNum = (Number) allocation.get("roomID");
                if (roomNum != null) {
                    int roomId = roomNum.intValue();
                    roomCounts.put(roomId, roomCounts.getOrDefault(roomId, 0) + 1);
                }
            }
        }
        if (roomCounts.isEmpty()) {
            return null;
        }
        Integer leastUsedRoom = null;
        int lowestCount = Integer.MAX_VALUE;
        for (Map.Entry<Integer, Integer> entry : roomCounts.entrySet()) {
            if (entry.getValue() < lowestCount) {
                lowestCount = entry.getValue();
                leastUsedRoom = entry.getKey();
            }
        }
        return leastUsedRoom;
    }
    public List<Integer> getStaffWithThreeOrMoreCurrentPatients() {

        RestTemplate restTemplate = new RestTemplate();
        List<Map<String, Object>> allocations =
                restTemplate.getForObject(ALLOCATIONS_URL, List.class);
        Map<Integer, Integer> staffPatientCounts = new HashMap<>();
        if (allocations != null) {
            for (Map<String, Object> allocation : allocations) {
                Number employeeNum = (Number) allocation.get("employeeID");
                Object endTime = allocation.get("endTime");
                if (employeeNum != null && endTime == null) {
                    int employeeId = employeeNum.intValue();
                    staffPatientCounts.put(
                            employeeId,
                            staffPatientCounts.getOrDefault(employeeId, 0) + 1
                    );
                }
            }
        }
        List<Integer> busyStaff = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : staffPatientCounts.entrySet()) {
            if (entry.getValue() >= 3) {
                busyStaff.add(entry.getKey());
            }
        }
        return busyStaff;
    }
}