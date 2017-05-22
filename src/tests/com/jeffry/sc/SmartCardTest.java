package tests.com.jeffry.sc;

import org.junit.Test;
import static org.junit.Assert.*;

import com.licel.jcardsim.base.Simulator;

public class SmartCardTest {
	
	private Simulator CardSimulator = new Simulator();
	
	@Test
	public void TestCard(){
		assertNotNull("CardSimulator is null!", CardSimulator);
	}
}
