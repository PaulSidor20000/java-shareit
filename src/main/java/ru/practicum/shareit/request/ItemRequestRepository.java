package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByUserId(Long userId);

    @Query(
            "select ir" +
                    " from ItemRequest ir" +
                    " where ir.user.id not in :id"
    )
    Collection<ItemRequest> findAllExceptUserId(@Param("id") Long userId, PageRequest page);
}
