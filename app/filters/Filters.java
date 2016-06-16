package filters;

/**
 * Created by archana on 6/16/16.
 */
import play.mvc.EssentialFilter;
import play.filters.gzip.GzipFilter;
import play.http.HttpFilters;

import play.Logger;
import javax.inject.Inject;

public class Filters implements HttpFilters {

    private EssentialFilter[] filters;

    @Inject
    public Filters(GzipFilter gzipFilter) {
        filters = new EssentialFilter[] { gzipFilter.asJava() };
    }

    public EssentialFilter[] filters() {
        return filters;
    }
}