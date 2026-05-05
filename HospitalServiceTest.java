package com.example.hospitalapi;

import com.example.hospitalapi.service.HospitalService;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class HospitalServiceTest {

    @Test
    public void testReturnsRoomsForMatchingStaff() {
        HospitalService service = new HospitalService();
        List<Map<String, Object>> allocations = new ArrayList<>();
        allocations.add(Map.of("employeeID", 4, "roomID", 0));
        allocations.add(Map.of("employeeID", 6, "roomID", 1));
        allocations.add(Map.of("employeeID", 4, "roomID", 2));

        List<Integer> result = service.filterRoomsByStaff(allocations, 4);
        assertEquals(2, result.size());
        assertTrue(result.contains(0));
        assertTrue(result.contains(2));
    }
    @Test
    public void testReturnsEmptyListWhenStaffHasNoRooms() {
        HospitalService service = new HospitalService();
        List<Map<String, Object>> allocations = new ArrayList<>();
        allocations.add(Map.of("employeeID", 4, "roomID", 0));
        allocations.add(Map.of("employeeID", 6, "roomID", 1));
        List<Integer> result = service.filterRoomsByStaff(allocations, 99);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testDuplicateRoomsAreRemoved() {
        HospitalService service = new HospitalService();
        List<Map<String, Object>> allocations = new ArrayList<>();
        allocations.add(Map.of("employeeID", 4, "roomID", 0));
        allocations.add(Map.of("employeeID", 4, "roomID", 0));
        allocations.add(Map.of("employeeID", 4, "roomID", 0));
        List<Integer> result = service.filterRoomsByStaff(allocations, 4);
        assertEquals(1, result.size());
        assertTrue(result.contains(0));
    }
    @Test
    public void testNullAllocationListReturnsEmptyList() {
        HospitalService service = new HospitalService();
        List<Integer> result = service.filterRoomsByStaff(null, 4);
        assertTrue(result.isEmpty());
    }
}