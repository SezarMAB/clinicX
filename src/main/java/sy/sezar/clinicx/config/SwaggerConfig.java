package sy.sezar.clinicx.config;

import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;

@Configuration
public class SwaggerConfig {

    static {
        SpringDocUtils.getConfig().replaceWithClass(Pageable.class, SwaggerPageable.class);
    }

    public static class SwaggerPageable {
        private Integer page = 0;
        private Integer size = 20;
        private String sort = "id";

        public Integer getPage() { return page; }
        public void setPage(Integer page) { this.page = page; }
        public Integer getSize() { return size; }
        public void setSize(Integer size) { this.size = size; }
        public String getSort() { return sort; }
        public void setSort(String sort) { this.sort = sort; }
    }
}
