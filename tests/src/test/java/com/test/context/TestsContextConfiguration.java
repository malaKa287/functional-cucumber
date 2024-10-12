package com.test.context;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@SpringBootTest
@SpringBootConfiguration
@ComponentScan("com.test")
public class TestsContextConfiguration {
}
