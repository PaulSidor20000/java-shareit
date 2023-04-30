package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.TestEnvironment;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.RequestNotValidException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest extends TestEnvironment {
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

    @Test
    void createTest_whenOwnerFound_thenCreateItemAndReturnItemDto() {
        long ownerId = 2L;
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(mockItemMapper.merge(owner, itemDtoIn)).thenReturn(item);
        when(mockItemRepository.save(item)).thenReturn(item);
        when(mockItemMapper.mapOneForOwner(item)).thenReturn(itemDtoOut);

        ItemDto itemDtoActual = itemService.create(ownerId, itemDtoIn);

        assertEquals(itemDtoOut, itemDtoActual);
        verify(mockItemRepository).save(item);
    }

    @Test
    void createTest_whenOwnerNotFound_thenEntityNotFoundExceptionThrown() {
        long userId = 1L;
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.create(userId, itemDtoIn));
        verify(mockItemRepository, never()).save(item);
    }

    @Test
    void readTest_whenItemOfOwnerFound_thenReturnItemDtoForOwner() {
        long itemId = 1L;
        long ownerId = 2L;
        when(mockItemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(mockItemMapper.mapOneForOwner(item)).thenReturn(itemDtoOut);

        ItemDto itemDtoActual = itemService.read(itemId, ownerId);

        assertEquals(itemDtoOut, itemDtoActual);
        verify(mockItemMapper, never()).mapForUser(item);
    }

    @Test
    void readTest_whenItemForUserFound_thenReturnItemDtoForUser() {
        long itemId = 1L;
        long userId = 1L;
        when(mockItemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(mockItemMapper.mapForUser(item)).thenReturn(itemDtoOut);

        ItemDto itemDtoActual = itemService.read(itemId, userId);

        assertEquals(itemDtoOut, itemDtoActual);
        verify(mockItemMapper, never()).mapOneForOwner(item);
    }

    @Test
    void readTest_whenItemNotFound_thenEntityNotFoundExceptionThrown() {
        long itemId = 1L;
        long userId = 1L;
        when(mockItemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.read(itemId, userId));
        verify(mockItemMapper, never()).mapOneForOwner(item);
        verify(mockItemMapper, never()).mapForUser(item);
    }

    @Test
    void updateTest_whenItemFound_AndUserIsOwner_thenUpdateItemAndReturnItemDtoToOwner() {
        long itemId = 1L;
        long ownerId = 2L;
        when(mockItemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(mockItemMapper.merge(item, itemDtoIn)).thenReturn(item);
        when(mockItemRepository.save(item)).thenReturn(item);
        when(mockItemMapper.mapOneForOwner(item)).thenReturn(itemDtoOut);

        ItemDto itemDtoActual = itemService.update(ownerId, itemId, itemDtoIn);

        assertEquals(itemDtoOut, itemDtoActual);
    }

    @Test
    void updateTest_whenItemFound_ButUserIsNotOwner_thenEntityNotFoundExceptionThrown() {
        long itemId = 1L;
        long userId = 1L;
        when(mockItemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(EntityNotFoundException.class, () -> itemService.update(userId, itemId, itemDtoIn));
        verify(mockItemRepository, never()).save(item);
    }

    @Test
    void updateTest_whenItemNotFound_thenEntityNotFoundExceptionThrown() {
        long itemId = 1L;
        long userId = 1L;
        when(mockItemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.update(userId, itemId, itemDtoIn));
        verify(mockItemRepository, never()).save(item);
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

        when(mockItemRepository.findItemsByOwnerIdAndFetchAllEntities(anyLong(), eq(page)))
                .thenReturn(List.of(item));
        when(mockItemRepository.findNextBookings(anySet())).thenReturn(List.of(nextBooking));
        when(mockItemRepository.findLastBookings(anySet())).thenReturn(List.of(lastBooking));
        when(mockItemMapper.mapForUser(item)).thenReturn(itemDtoOut);

        List<ItemDto> itemDtosActual = itemService.findAllItemsOfOwner(ownerId, from, size);

        assertEquals(itemDtoOut, itemDtosActual.get(0));
    }

    @Test
    void searchText_whenQueryNotEmpty_thenReturnCollectionOfItemDtoForUser() {
        int from = 0;
        int size = 20;
        String query = "дрЕль";
        PageRequest page = PageRequest.of(from, size);

        when(mockItemRepository.searchByNameAndDescription(query, page)).thenReturn(List.of(item));
        when(mockItemMapper.mapForUser(item)).thenReturn(itemDtoOut);

        List<ItemDto> itemDtosActual = itemService.search(query, from, size);

        assertEquals(itemDtoOut, itemDtosActual.get(0));
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
        long userId = 3L;
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.of(userWithBookings));
        when(mockItemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(mockCommentMapper.merge(userWithBookings, item, commentDto)).thenReturn(comment);
        when(mockCommentRepository.save(comment)).thenReturn(comment);
        when(mockCommentMapper.map(comment)).thenReturn(commentDto);

        CommentDto commentDtoActual = itemService.createComment(itemId, userId, commentDto);

        assertEquals(commentDto, commentDtoActual);
        verify(mockCommentRepository).save(comment);
    }

    @Test
    void createCommentTest_whenUserCantComment_thenValidationExceptionThrown() {
        long itemId = 2L;
        long userId = 3L;
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.of(userWithBookings));
        when(mockItemRepository.findById(anyLong())).thenReturn(Optional.of(item2));

        assertThrows(ValidationException.class, () -> itemService.createComment(itemId, userId, commentDto));
        verify(mockCommentRepository, never()).save(comment);
    }

    @Test
    void createCommentTest_whenItemNotFound_thenEntityNotFoundExceptionThrown() {
        long itemId = 1L;
        long userId = 1L;
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(mockItemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.createComment(itemId, userId, commentDto));
        verify(mockCommentRepository, never()).save(comment);
    }

    @Test
    void createCommentTest_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        long itemId = 1L;
        long userId = 1L;
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.createComment(itemId, userId, commentDto));
        verify(mockCommentRepository, never()).save(comment);
    }



}