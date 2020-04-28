package application.gateway.zuul.filter

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import com.netflix.zuul.exception.ZuulException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants

import java.nio.charset.StandardCharsets

class PostLogFilter extends ZuulFilter {
    private final static Logger LOG = LoggerFactory.getLogger(PostLogFilter.class)

    @Override
    String filterType() {
        FilterConstants.POST_TYPE
    }

    @Override
    int filterOrder() {
        0
    }

    @Override
    boolean shouldFilter() {
        true
    }

    @Override
    Object run() throws ZuulException {
        final RequestContext context = RequestContext.getCurrentContext();

        responseDataStream.withReader(
                StandardCharsets.UTF_8.name(),
                { responseData ->
                    if (responseDataStream == null)
                        LOG.info("Logger {}: empty response", this.getClass().getSimpleName())
                    LOG.info("BODY: {}", responseData)
                }
        )

//            context.setResponseBody(responseData)
    }
}
