package com.fctseek;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FctseekBackendApplication {

        public static void main(String[] args) {
                SpringApplication.run(FctseekBackendApplication.class, args);
                System.out.println("=======================================");
                System.out.println("FCT-SEEK Backend iniciado correctamente");
                System.out.println("=======================================");
        }

}
