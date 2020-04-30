package application.gateway.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Build a log filter which will log the request info at PRE stage of filter lifecycle
 */
@Slf4j
public class PreLogFilter extends ZuulFilter {

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
        return 1;
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
        log.info("Logger {}: send {} request to {}",
                this.getClass().getSimpleName(),
                httpRequest.getMethod(),
                httpRequest.getRequestURI());
        return null;
    }
}
