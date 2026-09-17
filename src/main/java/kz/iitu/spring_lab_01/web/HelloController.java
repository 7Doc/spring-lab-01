package kz.iitu.spring_lab_01.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;



@RestController
@RequestMapping("/api")
public class HelloController {

    @Value("${app.owner:unknown}")
    private String owner;

    @GetMapping("/hello")
    public Greeting hello(@RequestParam(defaultValue = "World") String name) {
        return new Greeting("Hello, " + name + "!", owner, LocalDateTime.now());
    }

    @GetMapping("/info")
    public Info info() {
        return new Info(owner,
                System.getProperty("java.version"),
                Runtime.getRuntime().availableProcessors());
    }

    @GetMapping("/fibonacci")
    public List<Integer> fibonacci(
            @RequestParam(defaultValue = "1") int n) {

        List<Integer> result = new ArrayList<>();

        int a = 0;
        int b = 1;

        for (int i = 0; i < n; i++) {
            result.add(a);
            int next = a + b;
            a = b;
            b = next;
        }

        return result;
    }


    public record Greeting(String message, String owner, LocalDateTime timestamp) { }

    public record Info(String owner, String javaVersion, int cpuCores) { }
}

