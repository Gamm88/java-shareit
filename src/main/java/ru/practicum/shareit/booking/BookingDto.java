package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    @NotBlank
    private long id; // ид бронирования
    @NotBlank
    private Date start; // дата начала аренды
    @NotBlank
    private Date end; // дата окончания аренды
    private Boolean confirmed; // подтверждение аренды, подтверждается владельцем, по умолчанию false.
    @NotBlank
    private Item item; // арендуемая вещь
    @NotBlank
    private User renter; // арендатор вещий
}
