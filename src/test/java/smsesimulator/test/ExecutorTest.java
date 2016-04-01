package smsesimulator.test;

import java.io.IOException;

import org.junit.Test;

import smsesimulator.Executor;

public class ExecutorTest {

	@Test
	public void createScenarioTest() throws IOException {
		Executor executor = new Executor();
		executor.createScenario("src/test/resources/scenario1.json");
	}

}
