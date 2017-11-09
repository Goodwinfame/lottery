package com.goodwin.controller;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.Bucket;
/**
 * Created by Superwen on 2017/3/31.
 */
public class OSSConfig {

    // endpoint为访问域名
    // 可以使用OSS域名、自定义域名（CNAME）、IP、STS作为endpoint
    // STS（介绍：https://help.aliyun.com/document_detail/31931.html?spm=5176.doc32010.2.6.u4i4ck）
    // 访问域名对照中心可参看；https://help.aliyun.com/document_detail/31837.html?spm=5176.doc32010.2.1.EwrB1F
    // 此处endpoint以杭州为例，其它region请按实际情况填写:http://oss-cn-hangzhou.aliyuncs.com

    protected static String endpoint = "oss-cn-hangzhou.aliyuncs.com";

    // accessKey请登录https://ak-console.aliyun.com/#/查看
    // accessKeyId和accessKeySecret为申请的秘钥

    protected static String accessKeyId = "";
    protected static String accessKeySecret = "";

    // 随机生成一个15位的BucketName,相当于命名空间（容器）的名称 "mybucket"+RandomUtils.getNumRandom(15)
    protected static String bucketName = "";

    protected static String localFilePath = "文件下载位置";

    // 创建OSSClient实例
    protected static OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);


    /**
     * 创建一个Bucket 存储空间
     * @param buckets 创建Bucket的名称
     * @return 创建的Bucket对象
     */
    public Bucket createBucket(String buckets){
        Bucket bucket = ossClient.createBucket(buckets);
        // 关闭client
        ossClient.shutdown();
        return bucket;
    }

}