package me.shouheng.notepal.fragment.album.entity;

import android.graphics.Color;

import java.util.Random;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.shouheng.notepal.fragment.album.utils.FilePathUtils;

/**
 * Created by yyd on 15-3-29.
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotoNote {
    public static final int NO_ID = -1;
    /**
     * ID
     */
    private int id = NO_ID;
    /**
     * 名字
     */
    private String photoName;
    /**
     * 照片创建时间
     */
    private long createdPhotoTime;
    /**
     * 照片编辑时间
     */
    private long editedPhotoTime;
    /**
     * 照片是否被选中
     */
    private boolean isSelected = false;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 创建笔记时间
     */
    private long createdNoteTime;
    /**
     * 最后修改笔记时间
     */
    private long editedNoteTime;
    /**
     * Category中的类别
     */
    private int categoryId;
    /**
     * 标记
     */
    private int tag;
    /**
     * 颜色
     */
    private int mPaletteColor = Color.argb(255, new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255));

    public PhotoNote(String photoName) {
        this.photoName = photoName;
    }

    public PhotoNote(String photoName, long createdPhotoTime, long editedPhotoTime,
                     String title, String content, long createdNoteTime,
                     long editedNoteTime, int categoryId) {
        this.id = NO_ID;
        this.photoName = photoName;
        this.createdPhotoTime = createdPhotoTime;
        this.editedPhotoTime = editedPhotoTime;
        this.title = title;
        this.content = content;
        this.createdNoteTime = createdNoteTime;
        this.editedNoteTime = editedNoteTime;
        this.categoryId = categoryId;
    }

    public PhotoNote(int id, String photoName, long createdPhotoTime, long editedPhotoTime,
                     String title, String content, long createdNoteTime,
                     long editedNoteTime, int categoryId) {
        this.id = id;
        this.photoName = photoName;
        this.createdPhotoTime = createdPhotoTime;
        this.editedPhotoTime = editedPhotoTime;
        this.title = title;
        this.content = content;
        this.createdNoteTime = createdNoteTime;
        this.editedNoteTime = editedNoteTime;
        this.categoryId = categoryId;
    }

    public String getSmallPhotoPathWithFile() {
        return "file://" + FilePathUtils.getSmallPath() + photoName;
    }

    public String getBigPhotoPathWithFile() {
        return "file://" + FilePathUtils.getPath() + photoName;
    }

    public String getSmallPhotoPathWithoutFile() {
        return FilePathUtils.getSmallPath() + photoName;
    }

    public String getBigPhotoPathWithoutFile() {
        return FilePathUtils.getPath() + photoName;
    }

    public int getPaletteColor() {
        return mPaletteColor;
    }

    public void setPaletteColor(int paletteColor) {
        if (paletteColor != Color.WHITE) {
            mPaletteColor = paletteColor;
        }
    }

    public Object mTag;


}