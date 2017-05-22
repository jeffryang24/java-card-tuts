package tests.com.jeffry.sc;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import com.licel.jcardsim.base.Simulator;

public class SmartCardTest {
	
	private Simulator CardSimulator;
	
	@BeforeClass
	public void TestInit(){
		CardSimulator = new Simulator();
		assertNotNull("CardSimulator is null!", CardSimulator);
	}
	
	@Test
	public void TestCard(){
		
	}
	
	@AfterClass
	public void TearDownTest(){
		if (CardSimulator != null) CardSimulator = null;
	}
}
