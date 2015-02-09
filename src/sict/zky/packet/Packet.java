package sict.zky.packet;

import sict.zky.utils.AddHex;
import sict.zky.utils.CurrentTime;

public class Packet {
	private String s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12, s13;
	private AddHex ah = new AddHex();
	private String checksum, sum1, sum2, sum3;
	private String replyPacketString;
	private CurrentTime currentTime = new CurrentTime();

	public Packet() {
		super();
	}

	public void getcurrentTime() {
		currentTime.getCurrentTime();
		currentTime.TimeToHex();
		s4 = currentTime.getYearH();
		s5 = currentTime.getMonthH();
		s6 = currentTime.getDayH();
		s7 = currentTime.getHourH();
		s8 = currentTime.getMinuteH();
	}

	public String ReplyPacket() {
		s1 = "5a";
		s2 = "0b";
		s3 = "05";
		getcurrentTime();
		String checksum = ah.add(s1, s2, s3, s4, s5, s6, s7, s8);
		String sum1 = checksum.substring(0, 2);
		String sum2 = checksum.substring(2, 4);
		String sum3 = checksum.substring(4, 6);

		replyPacketString = s1 + s2 + s3 + s4 + s5 + s6 + s7 + s8 + checksum;
		// replyPacketString=s1+s2+s3+s4+s5+s6+s7+s8;
		return replyPacketString;
	}

	// ÅÐ¶ÏÊÇ·ñÎªÀ¶ÑÀ2.0ÑªÌÇÒÇ½á¹û°ü
	public boolean isInformationPacketofBGM(String InformationPacket) {

		if (!InformationPacket.substring(0, 2).equals("5A")) {
			return false;
		} else {
			if (InformationPacket.substring(26, 29).equals("000")) {
				return false;
			}

		}

		return true;
	}

	public boolean isInformationPacket(String InformationPacket) {

		if (!InformationPacket.substring(0, 2).equals("55")) {
			return false;
		} else {
			if (!InformationPacket.substring(2, 4).equals("10")) {
				return false;
			} else {
				if (!InformationPacket.substring(4, 6).equals("00")) {
					return false;
				}
			}

		}

		return true;
	}

	public boolean isStartPacket(String InformationPacket) {

		if (!InformationPacket.substring(0, 2).equals("55")) {
			return false;
		} else {
			if (!InformationPacket.substring(2, 4).equals("06")) {
				return false;
			} else {
				if (!InformationPacket.substring(4, 6).equals("01")) {
					return false;
				}
			}

		}

		return true;
	}

	public boolean isBGMResultPacket(String InformationPacket) {

		if (!InformationPacket.substring(0, 2).equals("55")) {
			return false;
		} else {
			if (!InformationPacket.substring(2, 4).equals("0E")) {
				return false;
			} else {
				if (!InformationPacket.substring(4, 6).equals("03")) {
					return false;
				}
			}

		}

		return true;
	}

	public boolean isBPMResultPacket(String InformationPacket) {

		if (!InformationPacket.substring(0, 2).equals("55")) {
			return false;
		} else {
			if (!InformationPacket.substring(2, 4).equals("0F")) {
				return false;
			} else {
				if (!InformationPacket.substring(4, 6).equals("03")) {
					return false;
				}
			}

		}

		return true;
	}

	public boolean isProcessPacket(String InformationPacket) {

		if (!InformationPacket.substring(0, 2).equals("55")) {
			return false;
		} else {
			if (!InformationPacket.substring(2, 4).equals("08")) {
				return false;
			} else {
				if (!InformationPacket.substring(4, 6).equals("02")) {
					return false;
				}
			}

		}

		return true;
	}
}
