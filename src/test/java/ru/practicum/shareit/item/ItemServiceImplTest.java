package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingShortImpl;
import ru.practicum.shareit.booking.model.BookStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.RequestNotValidException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private CommentRepository mockCommentRepository;
    @Mock
    private CommentMapper mockCommentMapper;
    @Mock
    private ItemMapper mockItemMapper;
    @InjectMocks
    private ItemServiceImpl itemService;
    private User user;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        User owner = new User();
        owner.setId(2L);

        item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        item.setOwner(owner);

        booking = new Booking();
        booking.setStatus(BookStatus.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setItem(item);
        booking.setBooker(user);
    }

    @Test
    void createTest_whenOwnerFound_thenCreateItemAndReturnItemDto() {
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(mockItemMapper.merge(any(User.class), any(ItemDto.class))).thenReturn(item);
        when(mockItemRepository.save(any(Item.class))).thenReturn(item);
        when(mockItemMapper.mapOneForOwner(any(Item.class))).thenReturn(new ItemDto());

        ItemDto itemDtoActual = itemService.create(1L, new ItemDto());

        assertEquals(new ItemDto(), itemDtoActual);
        verify(mockItemRepository).save(any(Item.class));
    }

    @Test
    void createTest_whenOwnerNotFound_thenEntityNotFoundExceptionThrown() {
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.create(1L, new ItemDto()));
        verify(mockItemRepository, never()).save(any(Item.class));
    }

    @Test
    void readTest_whenItemOfOwnerFound_thenReturnItemDtoForOwner() {
        long itemId = 1L;
        long ownerId = 2L;
        when(mockItemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(mockItemMapper.mapOneForOwner(any(Item.class))).thenReturn(new ItemDto());

        ItemDto itemDtoActual = itemService.read(itemId, ownerId);

        assertEquals(new ItemDto(), itemDtoActual);
        verify(mockItemMapper, never()).mapForUser(any(Item.class));
    }

    @Test
    void readTest_whenItemForUserFound_thenReturnItemDtoForUser() {
        long itemId = 1L;
        long userId = 1L;
        when(mockItemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(mockItemMapper.mapForUser(any(Item.class))).thenReturn(new ItemDto());

        ItemDto itemDtoActual = itemService.read(itemId, userId);

        assertEquals(new ItemDto(), itemDtoActual);
        verify(mockItemMapper, never()).mapOneForOwner(any(Item.class));
    }

    @Test
    void readTest_whenItemNotFound_thenEntityNotFoundExceptionThrown() {
        long itemId = 1L;
        long userId = 1L;
        when(mockItemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.read(itemId, userId));
        verify(mockItemMapper, never()).mapOneForOwner(any(Item.class));
        verify(mockItemMapper, never()).mapForUser(any(Item.class));
    }

    @Test
    void updateTest_whenItemFound_AndUserIsOwner_thenUpdateItemAndReturnItemDtoToOwner() {
        long itemId = 1L;
        long ownerId = 2L;
        when(mockItemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(mockItemMapper.merge(item, new ItemDto())).thenReturn(item);
        when(mockItemRepository.save(any(Item.class))).thenReturn(item);
        when(mockItemMapper.mapOneForOwner(any(Item.class))).thenReturn(new ItemDto());

        ItemDto itemDtoActual = itemService.update(ownerId, itemId, new ItemDto());

        assertEquals(new ItemDto(), itemDtoActual);
    }

    @Test
    void updateTest_whenItemFound_ButUserIsNotOwner_thenEntityNotFoundExceptionThrown() {
        long itemId = 1L;
        long userId = 1L;
        when(mockItemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(EntityNotFoundException.class, () -> itemService.update(userId, itemId, new ItemDto()));
        verify(mockItemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateTest_whenItemNotFound_thenEntityNotFoundExceptionThrown() {
        long itemId = 1L;
        long userId = 1L;
        when(mockItemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.update(userId, itemId, new ItemDto()));
        verify(mockItemRepository, never()).save(any(Item.class));
    }

    @Test
    void deleteTest_whenInvoke_thenCheckInvocationOfIt() {
        long ownerId = 2L;

        itemService.delete(ownerId);

        verify(mockItemRepository).deleteById(ownerId);
    }

    @Test
    void findAllItemsOfOwnerTest_whenInvoke_thenReturnCollectionOfItemDtoForOwner() {
        int from = 0;
        int size = 20;
        long ownerId = 2L;
        PageRequest page = PageRequest.of(from, size);

        when(mockItemRepository.findItemsByOwnerIdAndFetchAllEntities(anyLong(), eq(page))).thenReturn(List.of(item));
        when(mockItemRepository.findNextBookings(anySet())).thenReturn(List.of(new BookingShortImpl()));
        when(mockItemRepository.findLastBookings(anySet())).thenReturn(List.of(new BookingShortImpl()));
        when(mockItemMapper.mapForUser(any(Item.class))).thenReturn(new ItemDto());

        List<ItemDto> itemDtosActual = itemService.findAllItemsOfOwner(ownerId, from, size);

        assertEquals(new ItemDto(), itemDtosActual.get(0));
    }

    @Test
    void searchText_whenQueryNotEmpty_thenReturnCollectionOfItemDtoForUser() {
        int from = 0;
        int size = 20;
        String query = "дрЕль";
        PageRequest page = PageRequest.of(from, size);

        when(mockItemRepository.searchByNameAndDescription(anyString(), eq(page))).thenReturn(List.of(item));
        when(mockItemMapper.mapForUser(item)).thenReturn(new ItemDto());

        List<ItemDto> itemDtosActual = itemService.search(query, from, size);

        assertEquals(new ItemDto(), itemDtosActual.get(0));
        assertEquals(1, itemDtosActual.size());
    }

    @Test
    void searchText_whenQueryEmpty_thenReturnEmptyCollection() {
        int from = 0;
        int size = 20;
        String query = "";
        PageRequest page = PageRequest.of(from, size);

        List<ItemDto> itemDtosActual = itemService.search(query, from, size);

        assertEquals(List.of(), itemDtosActual);
        verify(mockItemRepository, never()).searchByNameAndDescription(query, page);
    }

    @Test
    void getPageTest_whenRequestDataNotValid_thenRequestNotValidExceptionThrown() {
        int from = -1;
        int size = 20;
        String query = "";
        PageRequest page = PageRequest.of(0, 1);

        assertThrows(RequestNotValidException.class, () -> itemService.search(query, from, size));
        verify(mockItemRepository, never()).searchByNameAndDescription(query, page);
    }

    @Test
    void createCommentTest_whenUserCanComment_thenSaveCommentAndReturnCommentDto() {
        long itemId = 1L;
        long userId = 1L;
        user.setBookings(Set.of(booking));
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(mockItemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(mockCommentMapper.merge(any(User.class), any(Item.class), any(CommentDto.class))).thenReturn(new Comment());
        when(mockCommentRepository.save(any(Comment.class))).thenReturn(new Comment());
        when(mockCommentMapper.map(any(Comment.class))).thenReturn(new CommentDto());

        CommentDto commentDtoActual = itemService.createComment(itemId, userId, new CommentDto());

        assertEquals(new CommentDto(), commentDtoActual);
        verify(mockCommentRepository).save(any(Comment.class));
    }

    @Test
    void createCommentTest_whenUserCantComment_thenValidationExceptionThrown() {
        long itemId = 1L;
        long userId = 1L;
        user.setBookings(Set.of(booking));
        booking.setStatus(BookStatus.REJECTED);
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(mockItemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> itemService.createComment(itemId, userId, any(CommentDto.class)));
        verify(mockCommentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createCommentTest_whenItemNotFound_thenEntityNotFoundExceptionThrown() {
        long itemId = 1L;
        long userId = 1L;
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(mockItemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.createComment(itemId, userId, any(CommentDto.class)));
        verify(mockCommentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createCommentTest_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        long itemId = 1L;
        long userId = 1L;
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.createComment(itemId, userId, any(CommentDto.class)));
        verify(mockCommentRepository, never()).save(any(Comment.class));
    }


}