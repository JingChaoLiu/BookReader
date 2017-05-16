package com.buaa.yushijie.bookreader.Services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import bean.BookBean;
import bean.UserBean;

/**
 * Created by yushijie on 17-5-5.
 */

public class DownLoadBookInfoService {
    private static final String TAG = "DownLoadBookInfoService";
    private static final String URL_GET_BOOK = "http://120.25.89.166/BookReaderServer/QueryBook?bookName=";
    private static final String URL_GET_USER_INFO="http://120.25.89.166/BookReaderServer/SendUserInfo?username=";
    private static final String URL_GET_BOOK_COMMENT="";

    HttpURLConnection conn = null;
    URL url = null;

    private void connectToServer(String path) throws Exception{
        url = new URL(path);
        conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(5000);
        conn.setDoOutput(true);
        conn.connect();
    }

    public List<BookBean> getBookInfo(String query){
        List<BookBean> res = new ArrayList<>();
        try{
            connectToServer(URL_GET_BOOK+EncodeAndDecode.encodeString(query));

            InputStream in = conn.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(in);
            BookBean b = null;
            while((b = (BookBean) ois.readObject()) != null){
                res.add(b);
            }
            Log.e("TAG", "getBookInfo: "+res.size() );
            in.close();
            ois.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(conn != null) conn.disconnect();
        }
        return res;
    }

    public Uri getImageURI(String path,File cache){
        try{
            String name = "cover_"+path.substring(path.indexOf("id=")+3);
            File file = new File(cache,name);
            if(file.exists()){
               return Uri.fromFile(file);
            }else{
                connectToServer(path);

                InputStream in = conn.getInputStream();
                BufferedInputStream bin = new BufferedInputStream(in);

                Bitmap bp = BitmapFactory.decodeStream(bin);
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bitBins = new BufferedOutputStream(fos);
                bp.compress(Bitmap.CompressFormat.JPEG,100,bitBins);
                bitBins.flush();
                fos.flush();

                in.close();
                bin.close();
                fos.close();
                if(conn != null) conn.disconnect();
                return Uri.fromFile(file);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public UserBean getUserInfo(String username){
        UserBean res = null;
        try{
            connectToServer(URL_GET_USER_INFO+java.net.URLEncoder.encode(username,"UTF-8"));
            ObjectInputStream ois = new ObjectInputStream(conn.getInputStream());
            UserBean ub;
            while((ub=(UserBean) ois.readObject())!=null){
                res = ub;
            }
            ois.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(conn!=null)conn.disconnect();
        }
        return res;
    }

//    public ArrayList<String> getComment(BookBean ub){
//        ArrayList<String>
//        try{
//            connectToServer(URL_GET_BOOK_COMMENT);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
}
