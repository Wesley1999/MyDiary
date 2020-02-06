package com.wangshaogang.mydiary.utils;

import lombok.Data;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.client.methods.DavMethod;
import org.apache.jackrabbit.webdav.client.methods.DeleteMethod;
import org.apache.jackrabbit.webdav.client.methods.MkColMethod;
import org.apache.jackrabbit.webdav.client.methods.PropFindMethod;
import org.apache.jackrabbit.webdav.client.methods.PutMethod;
import org.apache.jackrabbit.webdav.property.DavPropertySet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 *
 * 不涉及加密解密
 *
 * 依赖：
 * <dependency>
 * <groupId>org.apache.jackrabbit</groupId>
 * <artifactId>jackrabbit-standalone</artifactId>
 * <version>1.6.5</version>
 * </dependency>
 */

@Data
public class WebDAVUtil {

    private String url;

    private HttpClient client;

    /**
     * 构造方法，创建对象后，可以调用其中的方法
     *
     * @param url      WebDAV的目录，坚果云是 https://dav.jianguoyun.com/dav/
     * @param username 用户名
     * @param password 密码
     */
    public WebDAVUtil(String url, String username, String password) {
        this.url = url;
        this.client = new HttpClient();
        Credentials creds = new UsernamePasswordCredentials(username, password);
        client.getState().setCredentials(AuthScope.ANY, creds);
        client.getParams().setAuthenticationPreemptive(true);
    }

    /**
     * 测试密码登录信息是否正确
     * @return true正确 false错误
     */
    public boolean test() {
        GetMethod method = new GetMethod(url);
        try {
            client.executeMethod(method);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return method.getStatusCode() != 401;
    }

    /**
     * 创建目录
     *
     * @param path 目录路径，可以为多级目录，前后不需要斜杠
     */
    public void mkdir(String path) {
        try {
            // 新建文件夹
            String dirUrl = url + URLEncoder.encode(path, "utf-8");
            MkColMethod mkDir = new MkColMethod(dirUrl);
            this.client.executeMethod(mkDir);

            int statusCode = mkDir.getStatusCode();
            String statusText = mkDir.getStatusText();
            if (statusCode != 201) {
                throw new RuntimeException(statusText);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除目录或文件
     *
     * @param path 删除的目录或文件所在的目录
     * @param name 删除的目录或文件
     *             注意：文件与文件夹是不可能同名的，所以一个名称可以唯一确定一个目录或文件夹
     */
    public void delete(String path, String name) {
        try {
            String fileUrl = url + URLEncoder.encode(path + "/" + name, "utf-8");
            DeleteMethod davDelete = new DeleteMethod(fileUrl);
            client.executeMethod(davDelete);

            int statusCode = davDelete.getStatusCode();
            String statusText = davDelete.getStatusText();
//            if (statusCode != 201) {
                // statusCode = 404， statusText = Not Found 是要删除的文件或文件夹不存在
//                throw new RuntimeException(statusText);
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传文件
     *
     * @param file     上传的文件
     * @param path     上传的文件要保存的目录
     * @param fileName 上传的文件要保存的文件名，通常可用源文件名
     *                 调用可参考 webDAVUtil.upload(new File("C:\\Users\\DELL\\Desktop\\0XYX[`J{8E7MT7%`SY97FNE.png"), "test", "test.png");
     */
    public void upload(File file, String path, String fileName) {
        try {

            String uplaodFileUrl = url + URLEncoder.encode(path + "/" + fileName, "utf-8");
            PutMethod put = new PutMethod(uplaodFileUrl);
            FileInputStream fileInputStream = new FileInputStream(file);
            RequestEntity requestEntity = new InputStreamRequestEntity(fileInputStream);
            put.setRequestEntity(requestEntity);
            client.executeMethod(put);

            int statusCode = put.getStatusCode();
            String statusText = put.getStatusText();
            if (statusCode != 201 && statusCode != 204) {
                throw new RuntimeException(statusText);
            }
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载文件
     *
     * @param path     要下载的文件的路径
     * @param fileName 文件名称
     * @param saveDir  要保存的目录
     */
    public void download(String path, String fileName, String saveDir) {
        try {
            // 下载文件
            String fileUrl = url + URLEncoder.encode(path + "/" + fileName, "utf-8");
            GetMethod method = new GetMethod(fileUrl);
            client.executeMethod(method);

            int statusCode = method.getStatusCode();
            String statusText = method.getStatusText();
            if (statusCode != 201 && statusCode != 200) {
                throw new RuntimeException(statusText);
            }

            // 保存文件到磁盘
            String fileDir = saveDir;
            File fileDirObj = new File(fileDir);
            if (!fileDirObj.exists()) {
                fileDirObj.mkdirs();
            }

            File fileObj = new File(fileDirObj, fileName);
            FileOutputStream fops = new FileOutputStream(fileObj);
            fops.write(method.getResponseBody());
            fops.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件（不下载）
     * @param path 文件路径
     * @param fileName 文件名
     * @return 字符串
     */
    public String get(String path, String fileName) throws Exception {
        String fileUrl = url + URLEncoder.encode(path + "/" + fileName, "utf-8");
        GetMethod method = new GetMethod(fileUrl);
        client.executeMethod(method);

        int statusCode = method.getStatusCode();
        String statusText = method.getStatusText();
        if (statusCode != 201 && statusCode != 200) {
            throw new RuntimeException(statusText);
        }

        // 保存文件到磁盘（临时）
        String fileDir = UUID.randomUUID().toString();

        File file = new File(fileDir);
        FileOutputStream fops = new FileOutputStream(file);
        fops.write(method.getResponseBody());
        fops.close();

        StringBuffer stringBuffer = new StringBuffer();
        FileInputStream in = new FileInputStream(fileDir);
        Scanner sc = new Scanner(in, "GBK");
        while (sc.hasNextLine()) {
            stringBuffer.append(sc.nextLine());
        }
        sc.close();
        file.delete();
        return stringBuffer.toString();
    }

    /**
     * 此方法通用性不强
     * @return 所有的日记文件名称
     * @throws IOException 异常
     * @throws DavException 异常
     */

}
