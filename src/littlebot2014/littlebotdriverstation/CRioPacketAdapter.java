package littlebot2014.littlebotdriverstation;

import java.util.zip.CRC32;

import android.content.Context;

public class CRioPacketAdapter
{
	public byte[] data = new byte[1024];
	
	
	//Constants:
	static final  int JOY1=8, JOY2=16, JOY3=24, JOY4=32;
	static final int NOT_ESTOP = 64, ENABLED = 32, AUTO = 16;
	
	
	/*
	 * data[0] and data[1]: packet index
	 * data[2]: control byte: reset not_e_stop enabled auto fms_attached resync test fpga
	 * data[3]: digital input bits.
	 * data[4] and data[5]: team number
	 * data[6]: alliance, either red 'R' or blue 'B'
	 * data[7]: position
	 * data[8]: joystick 1 x value
	 * data[9]: joystick 1 y value
	 * data[14-15]: joystick 1 buttons bits backward format ...8 7 6 5 4 3 2 1
	 * data[1020] to data[1023]: CRC checksum.
	 */	
	public CRioPacketAdapter(Context context, int teamNum)
	{
		data[2] |= NOT_ESTOP; //Set the not_e_stop control bit.
		
		//Set the team number.
		data[4] = IntMan.int3(teamNum);
		data[5] = IntMan.int4(teamNum);
		
		//Diverstation version, not sure if it needs this or not,
		//I just used the numbers on the packets captured from the original
		//driver station.
		data[72] = (byte)0x30;
		data[73] = (byte)0x31;
		data[74] = (byte)0x30;
		data[75] = (byte)0x34;
		data[76] = (byte)0x31;
		data[77] = (byte)0x34;
		data[78] = (byte)0x30;
		data[79] = (byte)0x30;
	}
	
	
	
	public void setIndex(int index)	//Set the packet index to the provided value.
	{
		data[0] = IntMan.int3(index);
		data[1] = IntMan.int4(index);
	}
	
	public void setAuto(boolean auto)	//Set robot to autonomous mode
	{
		if(auto)
			data[2] |= 16;
		else
			data[2] &= ~16;
	}
	public void setEnabled(boolean enabled)
	{
		if(enabled)
			data[2] |= 32;
		else
			data[2] &= ~32;
	}
	public void setDigitalIn(int port, boolean set)	//Set digital inputs, port is the
	{												//digital io port number with base 0.
		if(set)
			data[3] |= (byte)(128/Math.pow(2, port));
		else
			data[3] &= ~((byte)(128/Math.pow(2, port)));
	}
	
	public void setAlliance(char redOrBlue)	//Set alliance, either red 'R' or blue 'B'
	{
		data[6] = (byte) redOrBlue;
	}
	
	public void setPosition(int pos)	//Set team position.
	{
		data[7] = (byte)pos;
	}
	
	public void setJoystick(byte value, int axis, int joystick, boolean invert)	//Set the joystick values.
	{
		data[8+(joystick-1)*8+(axis-1)] = (byte) ((invert)?-value:value);
		int dbg = 0;
	}
	
	public void setButton(int joystick, int button, boolean set)
	{
		if(button <= 8){
			if(set)
				data[15 + (joystick-1)*8] |= (byte)(Math.pow(2, (button-1)));
			else
				data[15 + (joystick-1)*8] &= ~((byte)Math.pow(2, (button-1)));
		}else{
			if(set)
				data[14 + (joystick-1)*8] |= (byte)(Math.pow(2, button-9));
			else
				data[14 + (joystick-1)*8] &= ~((byte)Math.pow(2, button-9));
		}
		
	}
	
	public void setAnalog(int value, int analog)	//Set the joystick values.
	{
		int index = 0;
		switch(analog){
		case 1: index = 40; break;
		case 2: index = 42; break;
		case 3: index = 44; break;
		case 4: index = 46; break;
		}
		
		data[index] = IntMan.int3(value);
		data[index + 1] = IntMan.int4(value);
	}
	
	public void makeCRC()
	{
		clearCRC();
		CRC32 crc = new CRC32();
		crc.update(data);
		long checksum = crc.getValue();
		data[1020] = (byte)((checksum & 0xff000000)>>24);
		data[1021] = (byte)((checksum & 0xff0000)>>16);
		data[1022] = (byte)((checksum & 0xff00)>>8);
		data[1023] = (byte)(checksum & 0xff);
	}
	
	public void clearCRC(){
		data[1020] = 0;
		data[1021] = 0;
		data[1022] = 0;
		data[1023] = 0;
	}
}
