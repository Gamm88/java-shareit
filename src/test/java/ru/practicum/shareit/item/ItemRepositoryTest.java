package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import ru.practicum.shareit.item.model.item.Item;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;

    @Test
    void testSearchAvailableByText() {
        Item item = Item.builder()
                .name("item")
                .description("description")
                .available(true)
                .build();
        itemRepository.save(item);

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Item> items = itemRepository.searchByText("item", pageRequest);
        Assertions.assertTrue(items.get(0).getName().contains(item.getName()));
    }
}