package com.test;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

import com.bdd.config.StepsContextConfiguration;
import com.test.config.TestsContextConfiguration;

import io.cucumber.spring.CucumberContextConfiguration;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@CucumberContextConfiguration
@SpringBootTest(classes = {StepsContextConfiguration.class, TestsContextConfiguration.class})
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty,html:target/site/cucumber-pretty.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.bdd,com.test")
public class RunCucumberTest {
}

