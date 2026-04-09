package com.retail.pos.modules.inventory.adapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockOpnameRequest {
    @NotNull(message = "Batch ID is required")
    @JsonProperty("batch_id")
    private UUID batchId;

    @NotNull(message = "Physical quantity is required")
    @Min(value = 0, message = "Physical quantity must be at least 0")
    @JsonProperty("physical_qty")
    private Integer physicalQty;

    @NotBlank(message = "Reason is required")
    private String reason;

    @NotBlank(message = "Manager PIN is required")
    @JsonProperty("manager_pin")
    private String managerPin;
}
