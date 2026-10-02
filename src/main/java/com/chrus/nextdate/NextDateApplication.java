package com.chrus.nextdate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NextDateApplication {

    public static void main(String[] args) {
        SpringApplication.run(NextDateApplication.class, args);
    }

}

//   docker exec -it next-date-mysql mysql -u next-date-user -pnext-date-pass next-date