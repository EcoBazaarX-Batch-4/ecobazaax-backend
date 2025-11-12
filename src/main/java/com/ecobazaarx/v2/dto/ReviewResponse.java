package com.ecobazaarx.v2.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponse {
    private Long id;
    private Long productId;
    private String userName;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
