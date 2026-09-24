package kz.iitu.spring_lab_01.notify;

import kz.iitu.spring_lab_01.notify.Notifier;
import org.slf4j.*;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component("console")
@Order(1)
public class ConsoleNotifier implements Notifier {

    private static final Logger log = LoggerFactory.getLogger(ConsoleNotifier.class);

    @Override
    public String send(String message) {
        log.info("CONSOLE >> {}", message);
        return "console: " + message;
    }

    @Override
    public String channel() { return "console"; }
}

@Component("email")
@Primary                                   // chosen by default
@Order(2)
class EmailNotifier implements Notifier {

    private static final Logger log = LoggerFactory.getLogger(EmailNotifier.class);

    @Override
    public String send(String message) {
        log.info("EMAIL >> {}", message);
        return "email: " + message;
    }

    @Override
    public String channel() { return "email"; }
}

@Component("noop")
@Fallback                                  // fallback option (Spring 6.2+)
@Order(99)
class NoopNotifier implements Notifier {

    @Override
    public String send(String message) { return "noop"; }

    @Override
    public String channel() { return "noop"; }
}
