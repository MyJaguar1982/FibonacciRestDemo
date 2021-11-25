package com.emc.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.emc.test.rest.FibonacciCalculationResource;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = FibonacciRestServiceApplication.class)
@WebAppConfiguration
public class FibonacciRestServiceApplicationTests {

	private MockMvc restFibonacciMvc;

	@PostConstruct
	public void setup() {
		MockitoAnnotations.initMocks(this);
		FibonacciCalculationResource cal = new FibonacciCalculationResource();
		this.restFibonacciMvc = MockMvcBuilders.standaloneSetup(cal).build();
	}

	@Test
	public void verifyFibonacci() throws Exception {

		restFibonacciMvc.perform(get("/v1/rest/fibonacci/7"))
				.andExpect(status().isOk())
				.andExpect(content().bytes("0 1 1 2 3 5 8 ".getBytes()));
		
		restFibonacciMvc.perform(get("/v1/rest/fibonacci/-1"))
		.andExpect(status().is4xxClientError())
		.andExpect(content().bytes("Invalid number - -1".getBytes()));
		
		restFibonacciMvc.perform(get("/v1/rest/fibonacci/a"))
		.andExpect(status().is4xxClientError())
		.andExpect(content().bytes("Invalid number - a".getBytes()));
	}

}
