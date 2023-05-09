package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.RequestNotValidException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
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
    private ItemRequest itemRequest;
    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@mail.com");

        item = new Item();
        item.setId(1L);
        item.setName("Item1");
        item.setDescription("Item1 Description");
        item.setAvailable(true);
        item.setRequestId(1L);

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
    }

    @Test
    void createTest_whenDataValid_thenReturnItemRequestDto() {
        long userId = 1L;
        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mockItemRequestMapper.merge(any(User.class), any(ItemRequestDto.class))).thenReturn(new ItemRequest());
        when(mockItemRequestRepository.save(new ItemRequest())).thenReturn(new ItemRequest());
        when(mockItemRequestMapper.map(new ItemRequest())).thenReturn(new ItemRequestDto());

        ItemRequestDto itemRequestDtoActual = itemRequestService.create(userId, new ItemRequestDto());

        assertEquals(new ItemRequestDto(), itemRequestDtoActual);
        verify(mockItemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    void createTest_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        long userId = 1L;
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.create(userId, new ItemRequestDto()));
        verify(mockItemRequestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void readTest_whenDataValid_thenReturnCollectionItemRequestDto() {
        long userId = 1L;
        long requestId = 1L;
        when(mockUserRepository.existsById(anyLong())).thenReturn(true);
        when(mockItemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(mockItemRepository.findAllItemsByRequestIds(anySet())).thenReturn(List.of(new Item()));
        when(mockItemRequestMapper.merge(anyList(), any(ItemRequest.class))).thenReturn(new ItemRequestDto());

        ItemRequestDto itemRequestDtoActual = itemRequestService.read(requestId, userId);

        assertEquals(new ItemRequestDto(), itemRequestDtoActual);
    }

    @Test
    void readTest_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        long userId = 1L;
        long requestId = 1L;
        when(mockUserRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.read(requestId, userId));
        verify(mockItemRequestMapper, never()).merge(anyList(), any(ItemRequest.class));
    }

    @Test
    void readTest_whenItemRequestNotFound_thenEntityNotFoundExceptionThrown() {
        long userId = 1L;
        long requestId = 1L;
        when(mockUserRepository.existsById(anyLong())).thenReturn(true);
        when(mockItemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.read(requestId, userId));
        verify(mockItemRequestMapper, never()).merge(anyList(), any(ItemRequest.class));
    }

    @Test
    void findAllRequestsOfUserTest_whenDataValid_thenReturnCollectionItemRequestDto() {
        long userId = 1L;
        when(mockUserRepository.existsById(anyLong())).thenReturn(true);
        when(mockItemRequestRepository.findByUserId(anyLong())).thenReturn(List.of(itemRequest));
        when(mockItemRepository.findAllItemsByRequestIds(Set.of(1L))).thenReturn(List.of(item));
        when(mockItemRequestMapper.merge(List.of(item), itemRequest)).thenReturn(new ItemRequestDto());

        Collection<ItemRequestDto> itemRequestDtosActual = itemRequestService.findAllRequestsOfUser(userId);

        assertEquals(List.of(new ItemRequestDto()), itemRequestDtosActual);
        verify(mockItemRequestMapper).merge(anyList(), any(ItemRequest.class));
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
        when(mockUserRepository.existsById(anyLong())).thenReturn(true);
        when(mockItemRequestRepository.findAllExceptUserId(userId, page)).thenReturn(List.of(itemRequest));
        when(mockItemRepository.findAllItemsByRequestIds(Set.of(1L))).thenReturn(List.of(item));
        when(mockItemRequestMapper.merge(List.of(item), itemRequest)).thenReturn(new ItemRequestDto());

        Collection<ItemRequestDto> itemRequestDtosActual = itemRequestService.findAllRequestsOfOthers(userId, page);

        assertEquals(List.of(new ItemRequestDto()), itemRequestDtosActual);
        verify(mockItemRequestMapper).merge(anyList(), any(ItemRequest.class));
    }

    @Test
    void findAllRequestsOfOthersTest_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        int from = 0;
        int size = 20;
        Long userId = 1L;
        PageRequest page = PageRequest.of(from, size);
        when(mockUserRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.findAllRequestsOfOthers(userId, page));
        verify(mockItemRequestMapper, never()).merge(anyList(), any(ItemRequest.class));
    }

    @Disabled("Validation was removed from this module")
    @Test
    void findAllRequestsOfOthersTest_whenPageDataNotValid_thenRequestNotValidExceptionThrown() {
        int from = -1;
        int size = 20;
        Long userId = 1L;
        PageRequest page = PageRequest.of(from, size);

        assertThrows(RequestNotValidException.class, () -> itemRequestService.findAllRequestsOfOthers(userId, page));
        verify(mockItemRequestMapper, never()).merge(anyList(), any(ItemRequest.class));
    }

}