package com.retail.pos.core.web.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebResponse<T> {

    private boolean success;

    private String message;

    private T data;

    private Object metadata;

    private Object error;

    private String timestamp;

    public static <T> WebResponse<T> success(T data, String message) {
        return WebResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(OffsetDateTime.now().toString())
                .build();
    }

    public static <T> WebResponse<T> error(String message, Object error) {
        return WebResponse.<T>builder()
                .success(false)
                .message(message)
                .error(error)
                .timestamp(OffsetDateTime.now().toString())
                .build();
    }
}
