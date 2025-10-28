package com.agentnurse.model.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRoleTest {

    @Test
    void userRole_HasCorrectValues() {
        // Assert
        assertEquals(4, UserRole.values().length);
        assertNotNull(UserRole.PATIENT);
        assertNotNull(UserRole.FAMILY);
        assertNotNull(UserRole.CAREGIVER);
        assertNotNull(UserRole.ADMIN);
    }

    @Test
    void getDisplayName_ReturnsCorrectDisplayName() {
        // Assert
        assertEquals("患者", UserRole.PATIENT.getDisplayName());
        assertEquals("家属", UserRole.FAMILY.getDisplayName());
        assertEquals("护理员", UserRole.CAREGIVER.getDisplayName());
        assertEquals("管理员", UserRole.ADMIN.getDisplayName());
    }

    @Test
    void valueOf_ReturnsCorrectEnum() {
        // Act & Assert
        assertEquals(UserRole.PATIENT, UserRole.valueOf("PATIENT"));
        assertEquals(UserRole.FAMILY, UserRole.valueOf("FAMILY"));
        assertEquals(UserRole.CAREGIVER, UserRole.valueOf("CAREGIVER"));
        assertEquals(UserRole.ADMIN, UserRole.valueOf("ADMIN"));
    }

    @Test
    void valueOf_WithInvalidValue_ThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> UserRole.valueOf("INVALID"));
    }

    @Test
    void name_ReturnsCorrectName() {
        // Assert
        assertEquals("PATIENT", UserRole.PATIENT.name());
        assertEquals("FAMILY", UserRole.FAMILY.name());
        assertEquals("CAREGIVER", UserRole.CAREGIVER.name());
        assertEquals("ADMIN", UserRole.ADMIN.name());
    }

    @Test
    void allRoles_HaveUniqueDisplayNames() {
        // Arrange
        UserRole[] roles = UserRole.values();
        
        // Assert
        for (int i = 0; i < roles.length; i++) {
            for (int j = i + 1; j < roles.length; j++) {
                assertNotEquals(roles[i].getDisplayName(), roles[j].getDisplayName());
            }
        }
    }

    @Test
    void enumComparison_WorksCorrectly() {
        // Act & Assert
        assertSame(UserRole.PATIENT, UserRole.valueOf("PATIENT"));
        assertEquals(UserRole.ADMIN, UserRole.ADMIN);
        assertNotEquals(UserRole.PATIENT, UserRole.FAMILY);
    }
}