package tests.com.jeffry.sc;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

import com.licel.jcardsim.io.CAD;
import com.licel.jcardsim.io.JavaxSmartCardInterface;

import javacard.framework.AID;
import main.com.jeffry.sc.Hello;

public class SmartCardTest {
	
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
	}
	
	@Test
	public void TestInstallApplet(){
		AID InstallAppletResp = Simulator.installApplet(AppletID, Hello.class);
		assertNotNull("Failed to install applet!", InstallAppletResp);
		
		// selectApplet always return false while it succeeds selecting the applet
		// so, no need to assert this, or asserts with negation...
		Simulator.selectApplet(InstallAppletResp);
	}
	
	@Test
	public void TestSelectAPDU(){
		ResponseAPDU RespAPDU = Simulator.transmitCommand(new CommandAPDU(0x00, 0x40, 0x00, 0x00));
		assertEquals("Failed doing select APDU command!", 0x9000, RespAPDU.getSW());
		// Assert "Hello"
		assertEquals("'Hello' word is wrong!", "Hello", new String(RespAPDU.getData()));
	}
}
