package com.bjpowernode.p2p;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;

/**
 * @author 宋艾衡 on 2018/8/16 上午11:34
 */
public class QRCodeCreater {


	public void generateQRCode() throws IOException, WriterException {


		/**
		 * 使用fastJoson生成json对象
		 */

		JSONObject jsonObject = new JSONObject ();
		jsonObject.put ( "www.baidu.com","www.baidu.com" );
		JSONObject jsonObject1 = new JSONObject ();
		jsonObject1.put ( "innerJson","inner json" );
		jsonObject.put ( "jsonObject1",jsonObject1 );

		String content = jsonObject.toJSONString ();
		System.out.println (content);



		int width = 200;
		int hight = 200;

		Map<EncodeHintType,Object> hitMap = Maps.newHashMap ();
		hitMap.put ( EncodeHintType.CHARACTER_SET,"UTF-8" );

//		创建矩阵对象
		BitMatrix bitMatrix = new MultiFormatWriter ().encode ( content, BarcodeFormat.QR_CODE, width,hight,hitMap);

		String filePath = "/Users/song/IdeaProjects/DLJD/p2p/p2p-QRCode";
		String fileName = "qrCode.jpg";
		Path path = FileSystems.getDefault ().getPath ( filePath,fileName );

		MatrixToImageWriter.writeToPath ( bitMatrix,"jpg",path );

		System.out.println ("生成成功");



	}


}
