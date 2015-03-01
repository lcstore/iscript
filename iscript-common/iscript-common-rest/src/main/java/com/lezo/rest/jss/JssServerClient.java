package com.lezo.rest.jss;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.jcloud.jss.Credential;
import com.jcloud.jss.JingdongStorageService;
import com.jcloud.jss.domain.Bucket;
import com.jcloud.jss.domain.ObjectSummary;

public class JssServerClient {
	private JingdongStorageService storageService;

	public JssServerClient(Credential credential) {
		storageService = new JingdongStorageService(credential);
	}

	public JssServerClient(String accessKey, String secretKey) {
		this(new Credential(accessKey, secretKey));
	}

	/**
	 * 获取用户桶列表
	 * 
	 * @return
	 */
	public List<Bucket> listAllBuckets() {
		// 调用获取桶列表API
		return storageService.listBucket();
	}

	/**
	 * 创建桶
	 * 
	 * @bucketName 桶名
	 * @return
	 */
	public void createBucket(String bucketName) {
		// 调用创建桶API
		storageService.createBucket(bucketName);
	}

	/**
	 * 删除桶
	 * 
	 * @bucketName 桶名
	 * @return
	 */
	public void deleteBucket(String bucketName) {
		// 调用删除桶API
		try {
			storageService.deleteBucket(bucketName);
		} catch (Exception e) {

		}
	}

	/**
	 * 删除存储对象
	 * 
	 * @param bucketName
	 *            桶名称
	 * @param objectName
	 *            对象名称
	 */
	public void deleteObject(String bucketName, String objectName) {
		// 调用删除存储对象API
		storageService.deleteObject(bucketName, objectName);
	}

	/**
	 * 获取存储对象列表
	 * 
	 * @param bucketName
	 *            桶名称
	 * @return 存储对象列表
	 */
	public List<ObjectSummary> listObjects(String bucketName) {
		// 此处调用云存储sdk接口：获取存储对象列表
		return storageService.bucket(bucketName).listObject().getObjectSummaries();
	}

	/**
	 * 获取对象输入流
	 * 
	 * @param bucketName
	 *            桶名
	 * @param objectName
	 *            对象名
	 * @return 对象输入流
	 */
	public InputStream downLoadFile(String bucketName, String objectName) {
		// 调用获取存储对象API
		return storageService.bucket(bucketName).object(objectName).get().getInputStream();
	}

	/**
	 * 上传对象(支持断点续传上传)
	 * (支持断点续传,对用户透明的,无其他操作,断网或者其他因素造成连接断开下次继续执行该方法即可云存储服务端会从上次断开的位置继续传输)
	 * 
	 * @param uploadify
	 *            文件
	 * @param uploadifyFileName
	 *            文件名
	 * @param albumName
	 *            相册名
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public void storageObject(File uploadify, String uploadifyFileName, String albumName)
			throws NoSuchAlgorithmException, IOException {
		// // 此处调用云存储sdk接口：上传对象（支持断点续传上传）
		// StorageObject object = null;
		// // 通过一个File对象实例化一个存储对象,可通过其他方式构造
		// object = new StorageObject(uploadify);
		// object.setKey(uploadifyFileName);
		// // 重新设置存储对象名称,可不设置
		// service.putObject(albumName, object);
		storageService.bucket(albumName).object(uploadifyFileName).entity(uploadify).put();
	}

	/**
	 * 上传对象(支持断点续传上传) (支持断点续传,对用户透明的,无其他操作,断网或者其他因素造成连接断开下次继续执行该方法即可
	 * 云存储服务端会从上次断开的位置继续传输) 通过文件流上传
	 * 
	 * @param is
	 *            文件流
	 * @param uploadifyFileName
	 *            文件名
	 * @param albumName
	 *            相册名
	 * @param fileLength
	 *            文件大小
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public void storageObjectByStream(InputStream is, String uploadifyFileName, String albumName, long fileLength)
			throws NoSuchAlgorithmException, IOException {
		// 此处调用云存储sdk接口：上传对象（支持断点续传上传）
		// StorageObject object = new StorageObject(uploadifyFileName);
		// object.setDataInputStream(is);
		// object.setContentLength(fileLength);
		// service.putObject(albumName, object);
		storageService.bucket(albumName).object(uploadifyFileName).entity(fileLength, is).put();// 必须指定流的长度,并且流不为空,length为流所在的文件大小
		is.close();
	}

	public boolean hasFile(String albumName, String fileName) {
		return storageService.bucket(albumName).object(fileName).exist();
	}
}
