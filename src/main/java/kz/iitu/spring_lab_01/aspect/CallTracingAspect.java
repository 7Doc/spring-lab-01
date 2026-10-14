package kz.iitu.spring_lab_01.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(4)
public class CallTracingAspect {

    private static final Logger log =
            LoggerFactory.getLogger(CallTracingAspect.class);

    private final ThreadLocal<Integer> depth =
            ThreadLocal.withInitial(() -> 0);

    @Before("kz.iitu.spring_lab_01.aspect.Pointcuts.serviceOperation()")
    public void traceBefore(JoinPoint jp) {

        String indent = "    ".repeat(depth.get());

        log.info("{}>> {}", indent,
                jp.getSignature().toShortString());

        depth.set(depth.get() + 1);
    }

    @After("kz.iitu.spring_lab_01.aspect.Pointcuts.serviceOperation()")
    public void traceAfter(JoinPoint jp) {

        depth.set(Math.max(0, depth.get() - 1));

        String indent = "    ".repeat(depth.get());

        log.info("{}<< {}", indent,
                jp.getSignature().toShortString());
    }
}

