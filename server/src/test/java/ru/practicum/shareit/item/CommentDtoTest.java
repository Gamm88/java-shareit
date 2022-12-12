package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.model.comment.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testItemDto() throws Exception {
        CommentDto commentDto = new CommentDto(
                1L, "Дрель грязная!", "Василий", LocalDateTime.of(2022, 12, 12, 15, 00));

        JsonContent<CommentDto> result = json.write(commentDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Дрель грязная!");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Василий");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2022-12-12T15:00:00");
    }
}
