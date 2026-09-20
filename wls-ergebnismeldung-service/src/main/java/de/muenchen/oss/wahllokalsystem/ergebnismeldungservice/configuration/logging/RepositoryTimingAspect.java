package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration.logging;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration.Profiles;
import lombok.val;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Aspect
@Profile(Profiles.PERFORMANCE_LOGGING)
@Component
public class RepositoryTimingAspect {
    private static final Logger PERFORMANCELOGGER = PerformanceLogging.createPerformanceLogger("repositories");


    @Around("execution(* de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain..*.*Repository+.*(..))")
    public Object measureCrudRepositoryMethods(final ProceedingJoinPoint pjp) throws Throwable {


        val stopWatch = StopWatch.createStarted();
        try {
            return pjp.proceed();
        } finally {
            stopWatch.stop();

            PERFORMANCELOGGER.info("[{}] {} took {} ms",
                    pjp.getSignature().getDeclaringType().getSimpleName(),
                    pjp.getSignature().getName(),
                    stopWatch.getDuration().toMillis());
        }
    }
}
