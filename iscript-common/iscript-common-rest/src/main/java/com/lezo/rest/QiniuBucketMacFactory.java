package com.lezo.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.qiniu.api.auth.digest.Mac;

public class QiniuBucketMacFactory {
	private static Map<String, QiniuBucketMac> qiniuBacketMacMap = new HashMap<String, QiniuBucketMac>();
	private static List<QiniuBucketMac> capacityMacList = new ArrayList<QiniuBucketMac>();
	static {
		QiniuBucketMac newBucketMac = null;
//		newBucketMac = new QiniuBucketMac(new Mac("dwCDiS4sTkm_8aXoesOHIvFKy65OdrBskcxThAmv", "vflRTCRrydngKk7QRYcTh7BYmsG-9KeH-NET4riL"), "istore", 10);
//		qiniuBacketMacMap.put(newBucketMac.getBucket(), newBucketMac);
		newBucketMac = new QiniuBucketMac(new Mac("fQvW5tvryZ9YoAPgOUH-AwjaFRJImsgekp8NgIrG", "SbZs6Nr3fZTP23I0vodwGkyOnYOdXN6PSzxvoxEy"), "d1002", 10);
		qiniuBacketMacMap.put(newBucketMac.getBucket(), newBucketMac);
		newBucketMac = new QiniuBucketMac(new Mac("9LRrNHyvLCeGLIreQ44VOPUgwyHsHEGFmto0CKtf", "vgkZsUExS4fc4Iym1FZPEgRe5BxItMnKmv0Ew0dk"), "d1001", 10);
		qiniuBacketMacMap.put(newBucketMac.getBucket(), newBucketMac);
		newBucketMac = new QiniuBucketMac(new Mac("VHr8cFrtqVO13Lpc2fuiaK6uejHuHccpIbIthzD6", "1kEswPEhbSaHxYsPkJPHKh7-uSA2VMsWdgOarGRT"), "p1001", 1);
		qiniuBacketMacMap.put(newBucketMac.getBucket(), newBucketMac);
		newBucketMac = new QiniuBucketMac(new Mac("o3UQWUGKjGlObA0b4Du0mvmihKNvw9W4CohNgQcO", "cFWwkPfCqj1Bd4qpVnujJ2ecmU_5XqpCh0udaYxZ"), "v1000", 1, "7vijww.com1.z0.glb.clouddn.com");
		qiniuBacketMacMap.put(newBucketMac.getBucket(), newBucketMac);
		newBucketMac = new QiniuBucketMac(new Mac("K5LxD4jMxDFgtBPA0KZAIIujt5yef1tvDDShqLcF", "oIdK7n4_FXDfSjroYL-JOu-3WFm-D0OjpHIvMp2z"), "p1002", 1, "7vijwt.com1.z0.glb.clouddn.com");
		qiniuBacketMacMap.put(newBucketMac.getBucket(), newBucketMac);
		newBucketMac = new QiniuBucketMac(new Mac("NQ6jBmqGpWWtQd4a50m4p3nRN7VgBqLwPuTBQwjt", "khu7d6vZyqciM-HI6OYDvokdipn562Kd9SycW4QH"), "d1003", 1, "7vijx7.com1.z0.glb.clouddn.com");
		qiniuBacketMacMap.put(newBucketMac.getBucket(), newBucketMac);

		for (QiniuBucketMac bucketMac : qiniuBacketMacMap.values()) {
			for (int i = 1; i <= bucketMac.getCapacity(); i++) {
				capacityMacList.add(bucketMac);
			}
		}
		Collections.shuffle(capacityMacList);
	}

	public static QiniuBucketMac getBucketMac(String bucket) {
		return qiniuBacketMacMap.get(bucket);
	}

	public static QiniuBucketMac getRandomBucketMac() {
		Random rand = new Random();
		int index = rand.nextInt(capacityMacList.size());
		return capacityMacList.get(index);
	}
}
