package kz.iitu.spring_lab_01.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

import kz.iitu.spring_lab_01.audit.Audited;

@Service
public class CatalogService {

    public String findById(long id) {
        sleep(50); // simulating a database call
        return "Item no. " + id;
    }

    @Audited(action = "CATALOG_LIST", logArguments = true)
    public List<String> findAll(int limit) {
        sleep(300); // a deliberately slow method
        return IntStream.rangeClosed(1, limit)
                .mapToObj(i -> "Item no. " + i)
                .toList();
    }

    @Audited(action = "CATALOG_REMOVE")
    public String remove(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "Invalid identifier: " + id);
        }
        return "Removed item no. " + id;
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    public String removeTwice(long id) {
        String first  = remove(id);      // self-invocation: via this, past the proxy
        String second = remove(id + 1);  // self-invocation as well
        return first + "; " + second;
    }

}