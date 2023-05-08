package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(Long ownerId, ItemRequestDto requestDto) {
        return post("", ownerId, requestDto);
    }

    public ResponseEntity<Object> read(Long itemId, Long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> update(Long ownerId, Long itemId, ItemRequestDto requestDto) {
        return patch("/" + itemId, ownerId, requestDto);
    }

    public void deleteById(Long itemId) {
        delete("/" + itemId);
    }

    public ResponseEntity<Object> findAllItemsOfOwner(Long ownerId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", ownerId, parameters);
    }

    public ResponseEntity<Object> search(String searchRequest, Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", searchRequest,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> createComment(Long itemId, Long bookerId, CommentRequestDto requestDto) {
        return post("/" + itemId + "/comment", bookerId, requestDto);
    }
}
