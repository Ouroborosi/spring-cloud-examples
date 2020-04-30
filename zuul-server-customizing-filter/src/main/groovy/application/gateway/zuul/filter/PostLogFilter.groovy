package application.gateway.zuul.filter

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import com.netflix.zuul.exception.ZuulException
import groovy.util.logging.Slf4j
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants
import org.springframework.util.StreamUtils

import java.nio.charset.StandardCharsets
import java.util.zip.GZIPInputStream

@Slf4j
class PostLogFilter extends ZuulFilter {
//    private final static Logger LOG = LoggerFactory.getLogger(PostLogFilter.class)

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

        String responseAsString = StreamUtils.copyToString(context.getResponseDataStream(), StandardCharsets.UTF_8);
        log.info("Logger PostLogFilter: {}", responseAsString)

        context.setResponseBody(responseAsString)
    }
}
