package main.com.jeffry.sc;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.Util;

public class Counter extends Applet{
	
	// Define variables
	public static final byte INS_GET_BALANCE = 0x02;
	public static final byte INS_CREDIT = 0x04;
	public static final byte INS_DEBIT = 0x06;
	
	public static final short MAX_BALANCE = 10000;
	public static final short MAX_CREDIT = 5000;
	public static final short MAX_DEBIT = 1000;
	
	private short balance;
	
	private Counter(){
		balance = 0;
	}

	public static void install( byte[] bArray, short bOffset, byte bLength  ) throws ISOException {
		(new Counter()).register();
	}
	
	@Override
	public void process(APDU apdu) throws ISOException {
		// TODO Auto-generated method stub
		byte[] buffer = apdu.getBuffer();
		
		if (selectingApplet()) return;
		
		switch(buffer[ISO7816.OFFSET_INS]){
		case INS_GET_BALANCE:
			getBalance(apdu,buffer);
			break;
			
		case INS_CREDIT:
			credit(apdu,buffer);
			break;
			
		case INS_DEBIT:
			debit(apdu,buffer);
			break;
			
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}
	
	private void getBalance(APDU apdu, byte[] buffer){
		Util.setShort(buffer, (short) 0, balance);
		apdu.setOutgoingAndSend((short)0, (short)2);
	}
	
	private void debit(APDU apdu, byte[] buffer){
		short debit;
		if (apdu.setIncomingAndReceive() != 2){
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
		}
		
		debit = Util.getShort(buffer, ISO7816.OFFSET_CDATA);
		
		if (debit > balance || debit <= 0 || debit > MAX_DEBIT){
			ISOException.throwIt(ISO7816.SW_WRONG_DATA);
		}
		
		balance -= debit;
		
		getBalance(apdu, buffer);
	}

	private void credit(APDU apdu, byte[] buffer){
		short credit;
		if (apdu.setIncomingAndReceive() != 2){
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
		}
		
		credit = Util.getShort(buffer, ISO7816.OFFSET_CDATA);
		
		if ((short)(credit+balance) > MAX_BALANCE || credit <= 0 || credit > MAX_CREDIT ){
			ISOException.throwIt(ISO7816.SW_WRONG_DATA);
		}
		
		balance += credit;
		
		getBalance(apdu, buffer);
	}
}
