package application.client.consumer.exception;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BusinessException extends HystrixBadRequestException {
    public BusinessException(String message) {
        super(message);
        log.error("BusinessException caused: {}", message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        log.error("BusinessException caused: {0}", cause);
    }
}
