package tests.com.jeffry.sc;

import static org.junit.Assert.assertNotNull;

import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

import com.licel.jcardsim.io.CAD;
import com.licel.jcardsim.io.JavaxSmartCardInterface;

import javacard.framework.AID;
import main.com.jeffry.sc.Counter;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SmardCardTest {
	/*
	 * Declaring variable
	 */
	private static CAD Cad;
	private static JavaxSmartCardInterface Simulator;
	private static AID AppletID;
	
	@BeforeClass
	public static void TestSetup(){
		System.setProperty("com.licel.jcardsim.terminal.type", "2");
		Cad = new CAD(System.getProperties());
		
		// Create Connection
		Simulator = (JavaxSmartCardInterface) Cad.getCardInterface();
		
		// Install Applet
		byte[] AppletIDs = new byte[]{
				(byte) 0xDA, (byte) 0xDE, (byte) 0xDF, 0x00, 0x00, 0x11
		};
		AppletID = new AID(AppletIDs, (short)0, (byte) AppletIDs.length);
		
		AID InstallAppletResp = Simulator.installApplet(AppletID, Counter.class);
		assertNotNull("Failed to install applet!", InstallAppletResp);
		
		// selectApplet always return false while it succeeds selecting the applet
		// so, no need to assert this, or asserts with negation...
		Simulator.selectApplet(InstallAppletResp);
	}
	
	@Test
	public void TestBalance(){
		// Make sure the balance is 0 first
		ResponseAPDU RespAPDU = Simulator.transmitCommand(new CommandAPDU(0x00, 0x02, 0x00, 0x00));
		assertEquals("Failed to get balance!", 0x9000, RespAPDU.getSW());
		byte[] expected = { 0x00, 0x00 };
		assertTrue("Balance is not zero at the beginning!", Arrays.equals(expected, RespAPDU.getData()));
	}
	
	@Test
	public void TestCredit(){
		// Add 100 bucks
		byte[] one_hundred_bucks = { 0x00, 0x64 };
		ResponseAPDU RespAPDU = Simulator.transmitCommand(new CommandAPDU(0x00,  0x04, 0x00, 0x00, one_hundred_bucks));
		assertEquals("Failed to get balance!", 0x9000, RespAPDU.getSW());
		assertTrue("Balance is not 100!", Arrays.equals(one_hundred_bucks, RespAPDU.getData()));
		
		// Add more 3000 bucks
		byte[] three_thousand_bucks = { 0x0B, (byte) 0xB8 };
		RespAPDU = Simulator.transmitCommand(new CommandAPDU(0x00,  0x04, 0x00, 0x00, three_thousand_bucks));
		assertEquals("Failed to get balance!", 0x9000, RespAPDU.getSW());
		assertTrue("Balance is not 3100!", Arrays.equals(ByteBuffer.allocate(2).putShort((short)3100).array(), RespAPDU.getData()));
	}
	
	@Test
	public void TestDebit(){
		// Debit 500 from 3100
		byte[] five_hundred_bucks = { 0x01, (byte)0xF4 };
		ResponseAPDU RespAPDU = Simulator.transmitCommand(new CommandAPDU(0x00,  0x06, 0x00, 0x00, five_hundred_bucks));
		assertEquals("Failed to get balance!", 0x9000, RespAPDU.getSW());
		assertTrue("Balance is not 2600!", Arrays.equals(ByteBuffer.allocate(2).putShort((short)2600).array(), RespAPDU.getData()));
	}
	
	@Test
	public void TestErrorCredit(){
		byte[] six_thousand_bucks = { 0x17, (byte) 0x70 };
		ResponseAPDU RespAPDU = Simulator.transmitCommand(new CommandAPDU(0x00,  0x04, 0x00, 0x00, six_thousand_bucks));
		assertEquals("SW 6A80 (Wrong Data) is not caught!", 0x6A80, RespAPDU.getSW());
	}
	
	@Test
	public void TestErrorDebit(){
		byte[] six_thousand_bucks = { 0x17, (byte) 0x70 };
		ResponseAPDU RespAPDU = Simulator.transmitCommand(new CommandAPDU(0x00,  0x06, 0x00, 0x00, six_thousand_bucks));
		assertEquals("SW 6A80 (Wrong Data) is not caught!", 0x6A80, RespAPDU.getSW());
	}
}
