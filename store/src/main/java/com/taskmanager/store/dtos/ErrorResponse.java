package com.taskmanager.store.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error response structure")
public class ErrorResponse {
    @Schema(description = "Error type", example = "about:blank")
    private String type;
    
    @Schema(description = "Error title", example = "Bad Request")
    private String title;
    
    @Schema(description = "HTTP status code", example = "400")
    private Integer status;
    
    @Schema(description = "Detailed error message", example = "Invalid status: INVALID. Valid values are: PENDING, COMPLETED")
    private String detail;
    
    @Schema(description = "Request URI", example = "/api/tasks")
    private String instance;
}