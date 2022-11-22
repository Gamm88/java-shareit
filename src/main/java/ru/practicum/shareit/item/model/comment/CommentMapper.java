package ru.practicum.shareit.item.model.comment;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.model.item.Item;

import java.util.ArrayList;
import java.util.List;

public class CommentMapper {
    //из CommentDto в Comment
    public static Comment mapToComment(CommentDto commentDto, Item item, User author) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .item(item)
                .author(author)
                .created(commentDto.getCreated())
                .build();
    }
    //из Comment в CommentDto
    public static CommentDto mapToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    //получение списка CommentDto из списка Comment
    public static List<CommentDto> mapToItemDto(Iterable<Comment> comments) {
        List<CommentDto> dtos = new ArrayList<>();
        for (Comment comment : comments) {
            dtos.add(mapToCommentDto(comment));
        }
        return dtos;
    }
}