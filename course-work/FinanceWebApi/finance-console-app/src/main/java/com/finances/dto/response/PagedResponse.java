package com.finances.dto.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {
    private List<T> content;
    private PageInfo page;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageInfo {
        private Integer size;
        private Integer number;
        private Long totalElements;
        private Integer totalPages;
    }

    // Helper methods to maintain compatibility
    public Integer getTotalPages() {
        return page != null ? page.totalPages : 1;
    }

    public Long getTotalElements() {
        return page != null ? page.totalElements : 0L;
    }

    public Integer getPageSize() {
        return page != null ? page.size : 10;
    }

    public Integer getPageNumber() {
        return page != null ? page.number : 0;
    }
}
