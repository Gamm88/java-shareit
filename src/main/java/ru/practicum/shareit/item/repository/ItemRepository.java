package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.item.Item;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "" +
            "SELECT i from Item i " +
            "where (LOWER(i.name)    like CONCAT('%',LOWER(?1),'%') " +
            "OR LOWER(i.description) like CONCAT('%',LOWER(?1),'%'))" +
            "AND i.available = true")
    List<Item> searchByText(String text, PageRequest pageRequest);

    List<Item> findAllByOwner(Long userId, PageRequest pageRequest);

    List<Item> findAllByRequest_Id(Long itemRequestId);
}