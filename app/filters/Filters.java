package filters;

/**
 * Created by archana on 6/16/16.
 */

import play.filters.cors.CORSFilter;
import play.filters.gzip.GzipFilter;
import play.http.HttpFilters;
import play.mvc.EssentialFilter;

import javax.inject.Inject;

public class Filters implements HttpFilters {

    private EssentialFilter[] filters;

    @Inject
    public Filters(GzipFilter gzipFilter, CORSFilter corsFilter) {
        filters = new EssentialFilter[] { gzipFilter.asJava(), corsFilter.asJava() };
    }

    public EssentialFilter[] filters() {
        return filters;
    }
}