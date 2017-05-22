package com.jeffry.sc;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.Util;

public class Hello extends Applet {
	
	// declaring "Hello"
	private final static byte[] hello = {
		0x48, 0x65, 0x6c, 0x6c, 0x6f	
	};
	
	protected Hello(){
		register();
	}
	
	public static void install( byte[] bArray, short bOffset, byte bLength  ) throws ISOException {
		new Hello();
	}

	@Override
	public void process(APDU apdu) throws ISOException {
		// TODO Auto-generated method stub
		byte[] buf = apdu.getBuffer();
		
		switch (buf[ISO7816.OFFSET_INS]) {
		case 0x40:
			Util.arrayCopy(hello, (byte)0, buf, ISO7816.OFFSET_CDATA, (byte)5);
			apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, (byte)5);
			break;

		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
			break;
		}
	}
	
}
