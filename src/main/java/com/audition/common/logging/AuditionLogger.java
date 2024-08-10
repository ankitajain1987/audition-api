package com.audition.common.logging;

import java.util.Optional;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

@Component
public class AuditionLogger {

    public void info(final Logger logger, final String message) {
        if (logger.isInfoEnabled()) {
            logger.info(message);
        }
    }

    public void info(final Logger logger, final String message, final Object object) {
        if (logger.isInfoEnabled()) {
            logger.info(message, object);
        }
    }

    public void debug(final Logger logger, final String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
    }

    public void warn(final Logger logger, final String message) {
        if (logger.isWarnEnabled()) {
            logger.warn(message);
        }
    }

    public void error(final Logger logger, final String message) {
        if (logger.isErrorEnabled()) {
            logger.error(message);
        }
    }

    public void logErrorWithException(final Logger logger, final String message, final Exception e) {
        if (logger.isErrorEnabled()) {
            logger.error(message, e);
        }
    }

    public void logStandardProblemDetail(final Logger logger, final ProblemDetail problemDetail, final Exception e) {
        if (logger.isErrorEnabled()) {
            final var message = createStandardProblemDetailMessage(problemDetail);
            logger.error(message, e);
        }
    }

    public void logHttpStatusCodeError(final Logger logger, final String message, final Integer errorCode) {
        if (logger.isErrorEnabled()) {
            logger.error(createBasicErrorResponseMessage(errorCode, message) + "\n");
        }
    }


    public String createStandardProblemDetailMessage(final ProblemDetail problemDetail) {
        if (problemDetail == null) {
            return "No problem detail provided.";
        }

        final StringBuilder messageBuilder = new StringBuilder();
        appendIfNotBlank(messageBuilder, "Type: ", String.valueOf(problemDetail.getType()));
        appendIfNotBlank(messageBuilder, "Title: ", String.valueOf(problemDetail.getTitle()));
        appendIfNotBlank(messageBuilder, "Status: ", String.valueOf(problemDetail.getStatus()));
        appendIfNotBlank(messageBuilder, "Detail: ", String.valueOf(problemDetail.getDetail()));
        appendIfNotBlank(messageBuilder, "Instance: ", String.valueOf(problemDetail.getInstance()));
        appendIfNotEmpty(messageBuilder, String.valueOf(problemDetail.getProperties()));

        return Optional.of(messageBuilder.toString())
            .filter(s -> !s.isEmpty())
            .orElse("No additional details available.");
    }


    private String createBasicErrorResponseMessage(final Integer errorCode, final String message) {
        final StringBuilder messageBuilder = new StringBuilder();
        appendIfNotBlank(messageBuilder, "Status: ", String.valueOf(errorCode));
        appendIfNotBlank(messageBuilder, "Message: ", String.valueOf(message));

        return messageBuilder.toString();
    }

    private void appendIfNotEmpty(final StringBuilder messageBuilder, final Object value) {
        if (value != null && !value.toString().isEmpty()) {
            messageBuilder.append("Properties: ").append(value).append(" | ");
        }
    }

    private void appendIfNotBlank(final StringBuilder messageBuilder, final String key, final String value) {
        if (Strings.isNotBlank(value)) {
            messageBuilder.append(key).append(value).append(" | ");
        }
    }
}
