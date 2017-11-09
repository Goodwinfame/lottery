package com.goodwin.controller;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Superwen on 2017/3/31.
 */

@Controller
public class LargeFileUploadController extends OSSConfig {
    //日志记录
//    private static final Logger LOG = LoggerFactory.getLogger(LargeFileUploadController.class);

    //获取配置文件
//    ResourceBundle resourceBundle = ResourceBundle.getBundle("config");

    //默认5个线程
    private static ExecutorService executorService = Executors.newFixedThreadPool(5);

    //存储OSS返回来的partETags，是一个线程同步的arraylist，靠这个去查询
    private static List<PartETag> partETags = Collections.synchronizedList(new ArrayList<PartETag>());

    //如果上传完毕，uploadId则失效，这里将client设为null，则进度查询会显示完毕（不是必须）
    private static OSSClient client = null;

    // 创建一个要存放的Object的名称，使用文件名怕重复，直接改名+存入数据库
    private static String key

    //默认一个静态的字段贮备，查询进度需要
    private static String uploadId;

    //默认一个静态的字段储备文件分割的数量，进度查询需要
    private static Integer partCount;

    //标识符，false情况下说名还没有开始，true则前端可以开始查询进度（不是必须）
    private boolean progress = false;

    // 每片最少5MB，这里写死了，最好用配置文件随时修改
    final long partSize = 5 * 1024 * 1024L;

    @ResponseBody
    @RequestMapping("upload")
    public void upload(@RequestParam(value = "file", required = false) MultipartFile mFile)
            throws IOException {
        //随便写一个文件名，这个是要和数据库关联的
        key = "time-1121";

        // MultipartFile转File，springmvc得到MultipartFile非常简单
        CommonsMultipartFile cf= (CommonsMultipartFile)mFile;
        DiskFileItem dFile = (DiskFileItem)cf.getFileItem();
        File file = dFile.getStoreLocation();

        // 构造OSSClient
        client = new OSSClient(endpoint, accessKeyId, accessKeySecret);

        try {
            // 1-获得uploadId
            uploadId = getUploadId(client, bucketName, key);

            // 2-断点续传分片
            partCount = getPartCount(file);

            // 3-开始配置子线程，开始上传多片文件到自己的bucket（oss的文件夹）中
            for (int i = 0; i < partCount; i++) {
                long startPos = i * partSize;
                long curPartSize = (i + 1 == partCount) ? (file.length() - startPos) : partSize;
                executorService.execute(new PartUploader(file, startPos, curPartSize, i + 1, uploadId));
            }

            // 4-方法执行中（只是步骤展示，这里没有代码）

            // 5-将标识符设为true，前端开始可以访问到上传进度
            progress = true;

            // 6-等待所有的线程上传完毕后关闭线程
            executorService.shutdown();
            while (!executorService.isTerminated()) {
                try {
                    //如果有线程没有完毕，等待5秒
                    executorService.awaitTermination(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // 7-判断是否所有的分片都上传完毕（不是必须）
            // judgePartCount();

            // 8-必须：在将所有数据Part都上传完成后对整个文件的分片验证。
            verification();

            // 9-成功，恢复初始值（恢复初始值是为了不让这次上传影响其他的文件上传）
            restoreDefaultValues();

            // 10-下载上传的文件（不是必须）
            //client.getObject(new GetObjectRequest(bucketName, key), new File(localFilePath));

        } catch (OSSException oe) {
            System.out.println("OSS错误原因：");
            System.out.println("Error Message: " + oe.getErrorCode());
            System.out.println("Error Code:       " + oe.getErrorCode());
            System.out.println("Request ID:      " + oe.getRequestId());
            System.out.println("Host ID:           " + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("OSSClient错误原因：");
            System.out.println("Error Message: " + ce.getMessage());
        } finally {
            //注意关闭资源
            if (client != null) {
                client.shutdown();
            }
        }
    }

    /**
     * 获得uploadId
     */
    public String getUploadId(OSSClient client, String bucketName, String key){
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, key);
        InitiateMultipartUploadResult result = client.initiateMultipartUpload(request);
        return result.getUploadId();
    }

    /**
     * 断点续传分片数量判断
     */
    public int getPartCount(File file){
        long fileLength = file.length();
        int partCount = (int) (fileLength / partSize); // 分片个数，不能超过10000
        if (fileLength % partSize != 0) {
            partCount++;
        }
        if (partCount > 10000) {
            throw new RuntimeException("分片不能超过10000，请修改每片大小");
        } else {
            return partCount;
        }
    }

    /**
     * 子线程上传（多线程的上传效果提升明显，如果单线程则在for循环中一个个上传即可）
     */
    private static class PartUploader implements Runnable {
        private File localFile;
        private long startPos;
        private long partSize;
        private int partNumber;
        private String uploadId;
        public PartUploader(File localFile, long startPos, long partSize, int partNumber, String uploadId) {
            this.localFile = localFile;
            this.startPos = startPos;
            this.partSize = partSize;
            this.partNumber = partNumber;
            this.uploadId = uploadId;
        }
        @Override
        public void run() {
            InputStream instream = null;
            try {
                instream = new FileInputStream(this.localFile);
                instream.skip(this.startPos);

                UploadPartRequest uploadPartRequest = new UploadPartRequest();
                uploadPartRequest.setBucketName(bucketName);
                uploadPartRequest.setKey(key);
                uploadPartRequest.setUploadId(this.uploadId);
                uploadPartRequest.setInputStream(instream);
                uploadPartRequest.setPartSize(this.partSize);
                uploadPartRequest.setPartNumber(this.partNumber);

                UploadPartResult uploadPartResult = client.uploadPart(uploadPartRequest);
                synchronized (partETags) {
                    partETags.add(uploadPartResult.getPartETag());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (instream != null) {
                    try {
                        instream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 判断是否上传完毕，这里起始需要得到哪个错了，从哪个开始重新上传，获得part#，然后计算5M的大小上传
     * 这里只是单纯的记录一下，以后再完善
     * @return
     */
    public void judgePartCount(){
        if (partETags.size() != partCount) {
            System.out.println("一部分没有上传成功，需要从ListMultipartUploads里面查看上传数量");
            throw new IllegalStateException("Upload multiparts fail due to some parts are not finished yet");
        } else {
            System.out.println("上传成功，文件名称：" + key + "\n");
        }
    }

    /**
     * 验证文件是否完全，OSS必须走这个方法
     * 最后返回CompleteMultipartUploadResult，这里不继续深化了
     * @return
     */
    public void verification(){
        // 修改顺序
        Collections.sort(partETags, new Comparator<PartETag>() {
            @Override
            public int compare(PartETag p1, PartETag p2) {
                return p1.getPartNumber() - p2.getPartNumber();
            }
        });
        System.out.println("开始验证\n");
        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(bucketName, key, uploadId, partETags);
        client.completeMultipartUpload(completeMultipartUploadRequest);
    }

    /**
     * 恢复初始值
     * @return
     */
    public void restoreDefaultValues(){
        executorService = Executors.newFixedThreadPool(5);
        partETags = Collections.synchronizedList(new ArrayList<PartETag>());
        client = null;
        uploadId = null;
        partCount = 0;
    }

    /**
     * 页面上进度查询走这个方法
     * @return
     */
    @ResponseBody
    @RequestMapping("u_test")
    public Object u_test(){
        //获得OSS上面的已经好了的分片和总分片相除获得进度
        ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, key, uploadId);
        PartListing partListing;
        try {
            partListing = client.listParts(listPartsRequest);
            Integer nowPartCount = partListing.getParts().size();
            return (int)((double)nowPartCount/partCount * 100);
        }catch (Exception e){
            //如果标识符为true说明已经开始上传，设为100%；否则说明刚进入方法，还没有上传，设为1%
            if(progress)
                return "100";
            else
                return "1";
        }
    }
}