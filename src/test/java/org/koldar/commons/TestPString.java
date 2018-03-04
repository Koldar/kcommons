package org.koldar.commons;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestPString {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test01() {
		assertThat(PString.format("hello {0}! I'm {1}", "world", "Massimo"), is("hello world! I'm Massimo"));
	}
	
	@Test
	public void test02() {
		assertThat(PString.format("hello {}! I'm {}", "world", "Massimo"), is("hello world! I'm Massimo"));
	}
	
	@Test
	public void test03() {
		assertThat(PString.format("hello {0:%s}! I'm {1:%03d}", "world", 3), is("hello world! I'm 003"));
	}
	
	@Test
	public void test04() {
		assertThat(PString.format("hello {:%s}! I'm {:%03d}", "world", 3), is("hello world! I'm 003"));
	}
	
	@Test
	public void test05() {
		assertThat(PString.format("hello {name}! I'm {username}", "name", "world", "username", "Massimo"), is("hello world! I'm Massimo"));
	}
	
	@Test
	public void test06() {
		assertThat(PString.format("hello {name}! I'm {username:%03d}", "name", "world", "username", 3), is("hello world! I'm 003"));
	}

}
