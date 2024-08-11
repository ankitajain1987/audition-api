package com.audition.web.advice;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionControllerAdvice.class);
    public static final String DEFAULT_TITLE = "API Error Occurred";
    private static final String ERROR_MESSAGE = " Error Code from Exception could not be mapped to a valid HttpStatus Code - ";
    private static final String DEFAULT_MESSAGE = "API Error occurred. Please contact support or administrator.";


    private final AuditionLogger auditionLogger;

    public ExceptionControllerAdvice(AuditionLogger auditionLogger) {
        this.auditionLogger = auditionLogger;
    }


    @ExceptionHandler(HttpClientErrorException.class)
    ProblemDetail handleHttpClientException(final HttpClientErrorException e) {
        return createProblemDetail(e, e.getStatusCode());
    }

    @ExceptionHandler(SystemException.class)
    public ProblemDetail handleSystemException(final SystemException e) {
        final HttpStatusCode status = getHttpStatusCodeFromSystemException(e);
        return createProblemDetail(e, status);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        return createProblemDetail(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleMainException(final Exception e) {
        final HttpStatusCode status = getHttpStatusCodeFromException(e);
        return createProblemDetail(e, status);
    }

    private ProblemDetail createProblemDetail(final Exception exception, final HttpStatusCode statusCode) {
        final ProblemDetail problemDetail = ProblemDetail.forStatus(statusCode);
        problemDetail.setTitle(getTitleFromException(exception));
        problemDetail.setDetail(getMessageFromException(exception));
        return problemDetail;
    }

    private String getMessageFromException(final Exception exception) {
        if (StringUtils.isNotBlank(exception.getMessage())) {
            return exception.getMessage();
        }
        if (exception instanceof MethodArgumentTypeMismatchException methodArgumentTypeMismatchException) {
            return String.format("Invalid value for parameter '%s'. Expected type: %s.",
                methodArgumentTypeMismatchException.getName(),
                methodArgumentTypeMismatchException.getRequiredType());
        }
        return DEFAULT_MESSAGE;
    }

    private String getTitleFromException(final Exception exception) {
        if (exception instanceof SystemException systemException) {
            return systemException.getTitle();
        }
        return DEFAULT_TITLE;
    }

    private HttpStatusCode getHttpStatusCodeFromSystemException(final SystemException exception) {
        try {
            return HttpStatusCode.valueOf(exception.getStatusCode());
        } catch (IllegalArgumentException iae) {
            auditionLogger.info(LOG, ERROR_MESSAGE + exception.getStatusCode());
            return INTERNAL_SERVER_ERROR;
        }
    }

    private HttpStatusCode getHttpStatusCodeFromException(final Exception exception) {
        if (exception instanceof HttpClientErrorException httpClientErrorException) {
            return httpClientErrorException.getStatusCode();
        } else if (exception instanceof HttpRequestMethodNotSupportedException) {
            return METHOD_NOT_ALLOWED;
        }
        return INTERNAL_SERVER_ERROR;
    }
}

