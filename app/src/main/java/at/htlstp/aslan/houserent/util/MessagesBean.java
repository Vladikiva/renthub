package at.htlstp.aslan.houserent.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/** Resolves user facing texts from {@code messages.properties}. */
@Component
public class MessagesBean {

    private final MessageSource messageSource;

    public MessagesBean(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String get(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
