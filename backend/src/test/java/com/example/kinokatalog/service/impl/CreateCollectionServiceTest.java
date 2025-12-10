package com.example.kinokatalog.service.impl;

import com.example.kinokatalog.dto.CollectionDTO;
import com.example.kinokatalog.exception.InvalidDataException;
import com.example.kinokatalog.exception.NotFoundException;
import com.example.kinokatalog.exception.UnauthorizedException;
import com.example.kinokatalog.mapper.CollectionMapper;
import com.example.kinokatalog.persistence.sql.entity.CollectionEntity;
import com.example.kinokatalog.persistence.sql.entity.UserEntity;
import com.example.kinokatalog.persistence.sql.repository.CollectionSqlRepository;
import com.example.kinokatalog.persistence.sql.repository.UserSqlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.*;
import java.util.Optional;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateCollectionServiceTest {

    @Mock private UserSqlRepository userRepo;
    @Mock private CollectionSqlRepository collectionRepo;
    @Mock private CollectionMapper mapper;

    @InjectMocks
    private CollectionServiceSqlImpl service;

    private UserEntity user;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        user = new UserEntity();
        user.setId(1);
        user.setUsername("alice");
    }

    private CollectionEntity entity(String name, String desc) {
        CollectionEntity e = new CollectionEntity();
        e.setId(100);
        e.setUser(user);
        e.setName(name);
        e.setDescription(desc);
        return e;
    }

    private CollectionDTO dto(CollectionEntity e) {
        CollectionDTO d = new CollectionDTO();
        d.setId(e.getId());
        d.setUserId(e.getUser().getId());
        d.setName(e.getName());
        d.setDescription(e.getDescription());
        return d;
    }

    static Stream<Arguments> invalidUserInputs() {
        return Stream.of(
                Arguments.of(null, "Name", "desc", "alice"),
                Arguments.of(0, "Name", "desc", "alice"),
                Arguments.of(-1, "Name", "desc", "alice")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidUserInputs")
    void invalidUserIdRejected(Integer userId, String name, String desc, String auth) {
        assertThrows(UnauthorizedException.class,
                () -> service.createCollection(userId, name, desc, auth));
    }

    @ParameterizedTest
    @ValueSource(strings = {"bob"})
    void authenticatedUserMismatch(String authUser) {
        when(userRepo.findById(1)).thenReturn(Optional.of(owner("alice")));
        assertThrows(UnauthorizedException.class,
                () -> service.createCollection(1, "Name", "desc", authUser));
    }

    @ParameterizedTest
    @ValueSource(ints = {1})
    void userNotFound(int userId) {
        when(userRepo.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> service.createCollection(userId, "Name", "desc", "alice"));
    }

    static Stream<Arguments> invalidNames() {
        return Stream.of(
                Arguments.of((String) null),
                Arguments.of(""),
                Arguments.of("a".repeat(101)),
                Arguments.of("bad\u0001name")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidNames")
    void invalidNameRejected(String name) {
        when(userRepo.findById(1)).thenReturn(Optional.of(owner("alice")));
        assertThrows(InvalidDataException.class,
                () -> service.createCollection(1, name, "desc", "alice"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"MyCollection"})
    void duplicateNameRejected(String name) {
        when(userRepo.findById(1)).thenReturn(Optional.of(owner("alice")));
        when(collectionRepo.existsByUserIdAndName(1, name)).thenReturn(true);
        assertThrows(InvalidDataException.class,
                () -> service.createCollection(1, name, "desc", "alice"));
    }

    static Stream<Arguments> invalidDescriptions() {
        return Stream.of(
                Arguments.of("a".repeat(4001)),
                Arguments.of("bad\u0002text")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidDescriptions")
    void invalidDescriptionRejected(String desc) {
        when(userRepo.findById(1)).thenReturn(Optional.of(owner("alice")));
        assertThrows(InvalidDataException.class,
                () -> service.createCollection(1, "ValidName", desc, "alice"));
    }

    static Stream<Arguments> validDescriptions() {
        return Stream.of(
                Arguments.of((String) null),
                Arguments.of(""),
                Arguments.of("a"),
                Arguments.of("a".repeat(4000))
        );
    }


    @ParameterizedTest
    @MethodSource("validDescriptions")
    void validCollectionCreated(String desc) {
        when(userRepo.findById(1)).thenReturn(Optional.of(owner("alice")));
        when(collectionRepo.existsByUserIdAndName(1, "ValidName")).thenReturn(false);

        CollectionEntity saved = entity("ValidName", desc);
        when(collectionRepo.save(any())).thenReturn(saved);
        when(mapper.toDTO(saved)).thenReturn(dto(saved));

        CollectionDTO result = service.createCollection(1, "ValidName", desc, "alice");

        assertEquals("ValidName", result.getName());
        assertEquals(1, result.getUserId());
    }

    private static UserEntity owner(String username) {
        UserEntity u = new UserEntity();
        u.setId(1);
        u.setUsername(username);
        return u;
    }
}
