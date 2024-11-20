package com.example.demo.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Aspect
@Component
public class PerformanceAspect {

    private static final String DIAGRAMS_DIR = "diagrams/";
    private static final String FILE_NAME = "all_operations_diagram.puml";

    // Define pointcut for all methods in the service layer
    @Pointcut("execution(* com.example.demo.service..*(..))")
    public void serviceMethods() {}

    @Around("serviceMethods()")
    public Object logPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed(); // Execute the actual method

        long duration = System.currentTimeMillis() - startTime;

        appendToDiagram(methodName, duration);
        return result;
    }

    private void appendToDiagram(String methodName, long duration) {
        File directory = new File(DIAGRAMS_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File diagramFile = new File(DIAGRAMS_DIR + FILE_NAME);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(diagramFile, true))) {
            // If the file is new, add the PlantUML start tag
            if (diagramFile.length() == 0) {
                writer.write("@startuml\n");
            }

            // Append the performance data
            writer.write(String.format(
                "participant \"%s\"\n%s -> %s : Execution Time: %dms\n",
                methodName, methodName, methodName, duration
            ));

            // Add the end tag only if this is the last method
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try (BufferedWriter shutdownWriter = new BufferedWriter(new FileWriter(diagramFile, true))) {
                    shutdownWriter.write("@enduml\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
