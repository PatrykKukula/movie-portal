package pl.patrykkukula.MovieReviewPortal.View.Common.Constants;

import java.util.Collections;
import java.util.List;

public class PagedList<T> {
    private final List<T> fullList;
    private final int pageSize;

    public PagedList(List<T> fullList, int pageSize) {
        this.fullList = fullList;
        this.pageSize = pageSize;
    }
    public int getTotalPages() {
        return (int) Math.ceil((double) fullList.size() / pageSize);
    }
    public List<T> getPage(int pageNumber) {
        int start = pageNumber * pageSize;
        int end = Math.min(start + pageSize, fullList.size());
        if (start > end || start >= fullList.size()) {
            return Collections.emptyList();
        }
        return fullList.subList(start, end);
    }
    public boolean isValidPage(int pageNumber) {
        return pageNumber >= 0 && pageNumber < getTotalPages();
    }
}
