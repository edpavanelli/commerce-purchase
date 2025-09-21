package net.mycompany.commerce.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationFiltersDto {


    private Integer pageNumber;
    private Integer pageSize;
    
    
}
