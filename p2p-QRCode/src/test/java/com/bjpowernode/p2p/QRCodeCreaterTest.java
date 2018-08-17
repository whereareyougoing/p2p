package com.bjpowernode.p2p;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author 宋艾衡 on 2018/8/16 上午11:56
 */
public class QRCodeCreaterTest {
	@Test
	public void generateQRCode() throws Exception {
		QRCodeCreater qrCodeCreater = new QRCodeCreater ();
		qrCodeCreater.generateQRCode ();
	}

}