package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestor_IdOrderByCreatedDesc(Long userId);

    List<ItemRequest> findAllByRequestor_IdIsNotOrderByCreatedDesc(Long userId, PageRequest pageRequest);
}