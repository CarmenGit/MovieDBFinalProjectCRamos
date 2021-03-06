package es.cice.moviedbfinalproject.model;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValoracionesList {

    private Integer id;
    private Integer page;
    private List<Result> results = null;
    private Integer total_pages;
    private Integer total_results;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public Integer getTotalPages() {
        return total_pages;
    }

    public void setTotalPages(Integer totalPages) {
        this.total_pages = totalPages;
    }

    public Integer getTotalResults() {
        return total_results;
    }

    public void setTotalResults(Integer totalResults) {
        this.total_results = totalResults;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
