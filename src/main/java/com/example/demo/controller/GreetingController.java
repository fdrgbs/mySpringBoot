package com.example.demo.controller;

import com.example.demo.domain.Greeting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 */
@Slf4j
@RestController
public class GreetingController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    ExecutorService service = Executors.newFixedThreadPool(5);


    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

    @RequestMapping("/greetingFlush")
    public void greetingFlush(@RequestParam(value="name", defaultValue="World") String name,
                             HttpServletResponse response) {
        try {
            Greeting greeting = new Greeting(counter.incrementAndGet(),
                    String.format(template, name));
            PrintWriter writer = null;
            try {
                writer = response.getWriter();
                writer.write(greeting.toString());
                writer.flush();
            } catch (IOException e) {
                log.error("httpResponseToJsonp IOException:{}", (e));
            } finally {
                if (null != writer) {
                    writer.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/greetingAsync")
    public void greetingAsync(@RequestParam(value="name", defaultValue="World") String name,
                                  HttpServletResponse response) {
        log.info("main enter");
        service.execute(() -> {
            log.info("thread enter");
            Greeting greeting = new Greeting(counter.incrementAndGet(),
                    String.format(template, name));
            try {
                PrintWriter writer = null;
                try {
                    writer = response.getWriter();
                    writer.write(greeting.toString());
                    writer.flush();
                } catch (IOException e) {
                    log.error("httpResponseToJsonp IOException:{}", (e));
                } finally {
                    if (null != writer) {
                        writer.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }
}
