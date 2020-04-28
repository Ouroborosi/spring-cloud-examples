package application.gateway.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import javax.servlet.http.HttpServletRequest;

/**
 * Build a log filter which will log the request info at PRE stage of filter lifecycle
 */
public class PreLogFilter extends ZuulFilter {

    private final static Logger LOG = LoggerFactory.getLogger(PreLogFilter.class);

    /**
     * Define the type as PRE filter
     * @return filter type
     */
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    /**
     * Define the order
     * @return order to run
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * Define active status
     * @return active status
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        final RequestContext context = RequestContext.getCurrentContext();
        final HttpServletRequest httpRequest = context.getRequest();
        LOG.info("Logger {}: send {} request to {}",
                this.getClass().getSimpleName(),
                httpRequest.getMethod(),
                httpRequest.getRequestURL().toString());
        return null;
    }
}
