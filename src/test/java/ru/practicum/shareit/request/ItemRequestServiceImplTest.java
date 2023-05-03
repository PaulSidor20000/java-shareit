package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.TestEnvironment;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.RequestNotValidException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest extends TestEnvironment {
    @Mock
    private ItemRequestRepository mockItemRequestRepository;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private ItemRequestMapper mockItemRequestMapper;
    @Mock
    private ItemRepository mockItemRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void createTest_whenDataValid_thenReturnItemRequestDto() {
        long userId = 1L;
        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mockItemRequestMapper.merge(user, itemRequestDtoIn)).thenReturn(itemRequest);
        when(mockItemRequestRepository.save(itemRequest)).thenReturn(itemRequest);
        when(mockItemRequestMapper.map(itemRequest)).thenReturn(itemRequestDtoOut);

        ItemRequestDto itemRequestDtoActual = itemRequestService.create(userId, itemRequestDtoIn);

        assertEquals(itemRequestDtoOut, itemRequestDtoActual);
        verify(mockItemRequestRepository).save(itemRequest);
    }

    @Test
    void createTest_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        long userId = 1L;
        when(mockUserRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.create(userId, itemRequestDtoIn));
        verify(mockItemRequestRepository, never()).save(itemRequest);
    }

    @Test
    void readTest_whenDataValid_thenReturnCollectionItemRequestDto() {
        long userId = 1L;
        long requestId = 1L;
        when(mockUserRepository.existsById(userId)).thenReturn(true);
        when(mockItemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(mockItemRepository.findAllItemsByRequestIds(anySet())).thenReturn(List.of(item));
        when(mockItemRequestMapper.merge(List.of(item), itemRequest)).thenReturn(itemRequestDtoOut);

        ItemRequestDto itemRequestDtoActual = itemRequestService.read(requestId, userId);

        assertEquals(itemRequestDtoOut, itemRequestDtoActual);
    }

    @Test
    void readTest_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        long userId = 1L;
        long requestId = 1L;
        when(mockUserRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.read(requestId, userId));
        verify(mockItemRequestMapper, never()).merge(anyList(), eq(itemRequest));
    }

    @Test
    void readTest_whenItemRequestNotFound_thenEntityNotFoundExceptionThrown() {
        long userId = 1L;
        long requestId = 1L;
        when(mockUserRepository.existsById(userId)).thenReturn(true);
        when(mockItemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.read(requestId, userId));
        verify(mockItemRequestMapper, never()).merge(anyList(), eq(itemRequest));
    }

    @Test
    void findAllRequestsOfUserTest_whenDataValid_thenReturnCollectionItemRequestDto() {
        long userId = 1L;
        when(mockUserRepository.existsById(userId)).thenReturn(true);
        when(mockItemRequestRepository.findByUserId(userId)).thenReturn(List.of(itemRequest));
        when(mockItemRepository.findAllItemsByRequestIds(Set.of(1L))).thenReturn(List.of(item));
        when(mockItemRequestMapper.merge(List.of(item), itemRequest)).thenReturn(itemRequestDtoOut);

        Collection<ItemRequestDto> itemRequestDtosActual = itemRequestService.findAllRequestsOfUser(userId);

        assertEquals(List.of(itemRequestDtoOut), itemRequestDtosActual);
        verify(mockItemRequestMapper).merge(anyList(), eq(itemRequest));
    }

    @Test
    void findAllRequestsOfUserTest_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        long userId = 1L;
        when(mockUserRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.findAllRequestsOfUser(userId));
        verify(mockItemRequestMapper, never()).merge(anyList(), eq(itemRequest));
    }

    @Test
    void findAllRequestsOfOthersTest_whenDataValid_thenReturnCollectionItemRequestDto() {
        int from = 0;
        int size = 20;
        long userId = 1L;
        PageRequest page = PageRequest.of(from, size);
        when(mockUserRepository.existsById(userId)).thenReturn(true);
        when(mockItemRequestRepository.findAllExceptUserId(userId, page)).thenReturn(List.of(itemRequest));
        when(mockItemRepository.findAllItemsByRequestIds(Set.of(1L))).thenReturn(List.of(item));
        when(mockItemRequestMapper.merge(List.of(item), itemRequest)).thenReturn(itemRequestDtoOut);

        Collection<ItemRequestDto> itemRequestDtosActual = itemRequestService.findAllRequestsOfOthers(userId, from, size);

        assertEquals(List.of(itemRequestDtoOut), itemRequestDtosActual);
        verify(mockItemRequestMapper).merge(anyList(), eq(itemRequest));
    }

    @Test
    void findAllRequestsOfOthersTest_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        int from = 0;
        int size = 20;
        Long userId = 1L;
        when(mockUserRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.findAllRequestsOfOthers(userId, from, size));
        verify(mockItemRequestMapper, never()).merge(anyList(), eq(itemRequest));
    }

    @Test
    void findAllRequestsOfOthersTest_whenPageDataNotValid_thenRequestNotValidExceptionThrown() {
        int from = -1;
        int size = 20;
        Long userId = 1L;

        assertThrows(RequestNotValidException.class, () -> itemRequestService.findAllRequestsOfOthers(userId, from, size));
        verify(mockItemRequestMapper, never()).merge(anyList(), eq(itemRequest));
    }

}