package com.agentnurse.repository;

import com.agentnurse.model.entity.User;
import com.agentnurse.model.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_ExistingUser_ReturnsUser() {
        // Arrange
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(UserRole.PATIENT)
                .enabled(true)
                .emailVerified(false)
                .build();
        entityManager.persistAndFlush(user);

        // Act
        Optional<User> found = userRepository.findByUsername("testuser");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    void findByUsername_NonExistingUser_ReturnsEmpty() {
        // Act
        Optional<User> found = userRepository.findByUsername("nonexistent");

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void findByEmail_ExistingUser_ReturnsUser() {
        // Arrange
        User user = User.builder()
                .username("emailuser")
                .email("email@example.com")
                .password("password")
                .role(UserRole.FAMILY)
                .enabled(true)
                .emailVerified(false)
                .build();
        entityManager.persistAndFlush(user);

        // Act
        Optional<User> found = userRepository.findByEmail("email@example.com");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("email@example.com", found.get().getEmail());
    }

    @Test
    void findByUsernameOrEmail_WithUsername_ReturnsUser() {
        // Arrange
        User user = User.builder()
                .username("dualuser")
                .email("dual@example.com")
                .password("password")
                .role(UserRole.CAREGIVER)
                .enabled(true)
                .emailVerified(false)
                .build();
        entityManager.persistAndFlush(user);

        // Act
        Optional<User> found = userRepository.findByUsernameOrEmail("dualuser", "dualuser");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("dualuser", found.get().getUsername());
    }

    @Test
    void findByUsernameOrEmail_WithEmail_ReturnsUser() {
        // Arrange
        User user = User.builder()
                .username("emailfind")
                .email("emailfind@example.com")
                .password("password")
                .role(UserRole.ADMIN)
                .enabled(true)
                .emailVerified(false)
                .build();
        entityManager.persistAndFlush(user);

        // Act
        Optional<User> found = userRepository.findByUsernameOrEmail("emailfind@example.com", "emailfind@example.com");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("emailfind@example.com", found.get().getEmail());
    }

    @Test
    void existsByUsername_ExistingUser_ReturnsTrue() {
        // Arrange
        User user = User.builder()
                .username("existsuser")
                .email("exists@example.com")
                .password("password")
                .role(UserRole.PATIENT)
                .enabled(true)
                .emailVerified(false)
                .build();
        entityManager.persistAndFlush(user);

        // Act
        boolean exists = userRepository.existsByUsername("existsuser");

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByUsername_NonExistingUser_ReturnsFalse() {
        // Act
        boolean exists = userRepository.existsByUsername("nonexistent");

        // Assert
        assertFalse(exists);
    }

    @Test
    void existsByEmail_ExistingEmail_ReturnsTrue() {
        // Arrange
        User user = User.builder()
                .username("emailexists")
                .email("emailexists@example.com")
                .password("password")
                .role(UserRole.FAMILY)
                .enabled(true)
                .emailVerified(false)
                .build();
        entityManager.persistAndFlush(user);

        // Act
        boolean exists = userRepository.existsByEmail("emailexists@example.com");

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByEmail_NonExistingEmail_ReturnsFalse() {
        // Act
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Assert
        assertFalse(exists);
    }

    @Test
    void findByRole_ReturnsUsersWithRole() {
        // Arrange
        User patient1 = User.builder()
                .username("patient1")
                .email("patient1@example.com")
                .password("password")
                .role(UserRole.PATIENT)
                .enabled(true)
                .emailVerified(false)
                .build();
        
        User patient2 = User.builder()
                .username("patient2")
                .email("patient2@example.com")
                .password("password")
                .role(UserRole.PATIENT)
                .enabled(true)
                .emailVerified(false)
                .build();
        
        User admin = User.builder()
                .username("admin")
                .email("admin@example.com")
                .password("password")
                .role(UserRole.ADMIN)
                .enabled(true)
                .emailVerified(false)
                .build();
        
        entityManager.persist(patient1);
        entityManager.persist(patient2);
        entityManager.persist(admin);
        entityManager.flush();

        // Act
        List<User> patients = userRepository.findByRole(UserRole.PATIENT);

        // Assert
        assertEquals(2, patients.size());
        assertTrue(patients.stream().allMatch(u -> u.getRole() == UserRole.PATIENT));
    }

    @Test
    void findByEnabled_ReturnsEnabledUsers() {
        // Arrange
        User enabled1 = User.builder()
                .username("enabled1")
                .email("enabled1@example.com")
                .password("password")
                .role(UserRole.PATIENT)
                .enabled(true)
                .emailVerified(false)
                .build();
        
        User disabled = User.builder()
                .username("disabled")
                .email("disabled@example.com")
                .password("password")
                .role(UserRole.PATIENT)
                .enabled(false)
                .emailVerified(false)
                .build();
        
        entityManager.persist(enabled1);
        entityManager.persist(disabled);
        entityManager.flush();

        // Act
        List<User> enabledUsers = userRepository.findByEnabled(true);
        List<User> disabledUsers = userRepository.findByEnabled(false);

        // Assert
        assertEquals(1, enabledUsers.size());
        assertTrue(enabledUsers.get(0).getEnabled());
        assertEquals(1, disabledUsers.size());
        assertFalse(disabledUsers.get(0).getEnabled());
    }

    @Test
    void save_CreatesNewUser() {
        // Arrange
        User user = User.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("password")
                .role(UserRole.CAREGIVER)
                .enabled(true)
                .emailVerified(false)
                .build();

        // Act
        User saved = userRepository.save(user);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("newuser", saved.getUsername());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void delete_RemovesUser() {
        // Arrange
        User user = User.builder()
                .username("deleteuser")
                .email("delete@example.com")
                .password("password")
                .role(UserRole.PATIENT)
                .enabled(true)
                .emailVerified(false)
                .build();
        entityManager.persistAndFlush(user);
        Long userId = user.getId();

        // Act
        userRepository.delete(user);
        entityManager.flush();

        // Assert
        assertFalse(userRepository.findById(userId).isPresent());
    }
}