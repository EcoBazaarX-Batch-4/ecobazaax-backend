package com.ecobazaarx.v2.dto;

import com.ecobazaarx.v2.model.Category;
import com.ecobazaarx.v2.model.TransportZone;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
public class ProductCreateRequest {

    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl;
    private Integer categoryId;
    private List<ProductMaterialDto> materials;
    private List<ProductManufacturingDto> manufacturing;
    private List<ProductPackagingDto> packaging;
    private Integer transportZoneId;
}
