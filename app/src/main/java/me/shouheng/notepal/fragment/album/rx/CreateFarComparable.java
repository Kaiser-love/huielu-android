package me.shouheng.notepal.fragment.album.rx;


import java.util.Comparator;

import me.shouheng.notepal.fragment.album.entity.PhotoNote;

/**
 * Created by yuyidong on 15/7/23.
 */
class CreateFarComparable implements Comparator<PhotoNote> {
    @Override
    public int compare(PhotoNote lhs, PhotoNote rhs) {
        return (-(int) (lhs.getCreatedPhotoTime() - rhs.getCreatedPhotoTime()));
    }
}
