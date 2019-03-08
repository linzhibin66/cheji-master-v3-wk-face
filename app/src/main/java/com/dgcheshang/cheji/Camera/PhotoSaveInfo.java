package com.dgcheshang.cheji.Camera;

/**
 * @author 李昱辰 987424501@qq.com
 * @date 2019/2/11
 */
public class PhotoSaveInfo {
    private String photoNo;
    //照片保存路径
    private String path;
    public PhotoSaveInfo(String photoNo, String path) {
        this.photoNo = photoNo;
        this.path = path;
    }

    public String getPhotoNo() {
        return photoNo;
    }

    public void setPhotoNo(String photoNo) {
        this.photoNo = photoNo;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "PhotoSaveInfo{" +
                "photoNo='" + photoNo + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
