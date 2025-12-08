package com.example.kinokatalog.service.impl;

import com.example.kinokatalog.dto.CollectionDTO;
import com.example.kinokatalog.exception.ConflictException;
import com.example.kinokatalog.exception.InvalidDataException;
import com.example.kinokatalog.exception.NotFoundException;
import com.example.kinokatalog.persistence.sql.entity.CollectionEntity;
import com.example.kinokatalog.persistence.sql.entity.UserEntity;
import com.example.kinokatalog.persistence.sql.repository.CollectionSqlRepository;
import com.example.kinokatalog.persistence.sql.repository.UserSqlRepository;
import com.example.kinokatalog.mapper.CollectionMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CollectionServiceSqlImplTest {

    @Mock private CollectionSqlRepository collectionRepo;
    @Mock private UserSqlRepository userRepo;
    @Mock private CollectionMapper mapper;

    @InjectMocks private CollectionServiceSqlImpl service;

    private UserEntity user;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        user = new UserEntity();
        user.setId(1);
        user.setUsername("alice");
    }

    private CollectionDTO dto(Integer userId, String name, String desc) {
        CollectionDTO d = new CollectionDTO();
        d.setUserId(userId);
        d.setName(name);
        d.setDescription(desc);
        return d;
    }

    private CollectionEntity savedEntity() {
        CollectionEntity e = new CollectionEntity();
        e.setId(100);
        e.setUser(user);
        e.setName("My Collection");
        e.setDescription("desc");
        e.setCreatedAt(LocalDateTime.now());
        return e;
    }

    private CollectionDTO returnedDTO() {
        return new CollectionDTO(100, 1, "My Collection", "desc", "alice",
                LocalDateTime.now(), null);
    }

    @Test
    void validInput_success() {
        CollectionDTO input = dto(1, "Favorites", "Nice description");

        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(collectionRepo.existsByUserIdAndName(1, "Favorites")).thenReturn(false);

        CollectionEntity saved = savedEntity();
        when(collectionRepo.save(any())).thenReturn(saved);
        when(mapper.toDTO(saved)).thenReturn(returnedDTO());

        CollectionDTO result = service.createCollection(input);

        assertEquals(100, result.getId());
    }

    @Test
    void nullDto_invalid() {
        assertThrows(InvalidDataException.class,
                () -> service.createCollection(null));
    }

    @Test
    void nullUserId_invalid() {
        CollectionDTO input = dto(null, "Favs", "desc");
        assertThrows(InvalidDataException.class,
                () -> service.createCollection(input));
    }

    @Test
    void userIdLessThan1_invalid() {
        CollectionDTO input = dto(0, "Favs", "desc");
        assertThrows(InvalidDataException.class,
                () -> service.createCollection(input));
    }

    @Test
    void userNotFound_throwsNotFound() {
        CollectionDTO input = dto(1, "Favs", "desc");

        when(userRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.createCollection(input));
    }

    @Test
    void nullName_invalid() {
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        CollectionDTO input = dto(1, null, "desc");

        assertThrows(InvalidDataException.class, () -> service.createCollection(input));
    }

    @Test
    void blankName_invalid() {
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        CollectionDTO input = dto(1, "   ", "desc");

        assertThrows(InvalidDataException.class, () -> service.createCollection(input));
    }

    @Test
    void nameLength1_valid() {
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        CollectionDTO input = dto(1, "a", "desc");

        when(collectionRepo.existsByUserIdAndName(1, "a")).thenReturn(false);
        when(collectionRepo.save(any())).thenReturn(savedEntity());
        when(mapper.toDTO(any())).thenReturn(returnedDTO());

        assertDoesNotThrow(() -> service.createCollection(input));
    }

    @Test
    void nameLength100_valid() {
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        String name = "a".repeat(100);
        CollectionDTO input = dto(1, name, "desc");

        when(collectionRepo.existsByUserIdAndName(1, name)).thenReturn(false);
        when(collectionRepo.save(any())).thenReturn(savedEntity());
        when(mapper.toDTO(any())).thenReturn(returnedDTO());

        assertDoesNotThrow(() -> service.createCollection(input));
    }

    @Test
    void nameLength101_invalid() {
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        String name = "a".repeat(101);
        CollectionDTO input = dto(1, name, "desc");

        assertThrows(InvalidDataException.class,
                () -> service.createCollection(input));
    }

    @Test
    void duplicateName_conflict() {
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        CollectionDTO input = dto(1, "Favorites", "desc");

        when(collectionRepo.existsByUserIdAndName(1, "Favorites")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> service.createCollection(input));
    }

    @Test
    void nameContainsUnsafeCharacters_invalid() {
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        String unsafe = "Bad\u0001Name";
        CollectionDTO input = dto(1, unsafe, "desc");

        assertThrows(InvalidDataException.class,
                () -> service.createCollection(input));
    }

    @Test
    void nullDescription_valid() {
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        CollectionDTO input = dto(1, "Cool", null);

        when(collectionRepo.existsByUserIdAndName(1, "Cool")).thenReturn(false);
        when(collectionRepo.save(any())).thenReturn(savedEntity());
        when(mapper.toDTO(any())).thenReturn(returnedDTO());

        assertDoesNotThrow(() -> service.createCollection(input));
    }

    @Test
    void emptyDescription_valid() {
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        CollectionDTO input = dto(1, "Cool", "");

        when(collectionRepo.existsByUserIdAndName(1, "Cool")).thenReturn(false);
        when(collectionRepo.save(any())).thenReturn(savedEntity());
        when(mapper.toDTO(any())).thenReturn(returnedDTO());

        assertDoesNotThrow(() -> service.createCollection(input));
    }

    @Test
    void description4000_valid() {
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        String desc = "a".repeat(4000);
        CollectionDTO input = dto(1, "Cool", desc);

        when(collectionRepo.existsByUserIdAndName(1, "Cool")).thenReturn(false);
        when(collectionRepo.save(any())).thenReturn(savedEntity());
        when(mapper.toDTO(any())).thenReturn(returnedDTO());

        assertDoesNotThrow(() -> service.createCollection(input));
    }

    @Test
    void description4001_invalid() {
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        String desc = "a".repeat(4001);
        CollectionDTO input = dto(1, "Cool", desc);

        assertThrows(InvalidDataException.class,
                () -> service.createCollection(input));
    }

    @Test
    void descriptionUnsafe_invalid() {
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        String desc = "hello\u0001world";
        CollectionDTO input = dto(1, "Cool", desc);

        assertThrows(InvalidDataException.class,
                () -> service.createCollection(input));
    }
}
