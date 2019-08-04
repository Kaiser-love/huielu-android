package me.shouheng.notepal.fragment.album.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.fragment.album.entity.PhotoNote;
import me.shouheng.notepal.fragment.album.utils.ImageManager.ImageLoaderManager;
import me.shouheng.notepal.fragment.album.viewholder.PhotoViewHolder;

/**
 * Created by yuyidong on 15/10/14.
 */
public class AlbumAdapter extends RecyclerView.Adapter<PhotoViewHolder> {
    private Context mContext;
    private List<PhotoNote> mPhotoNoteList;

    private PhotoViewHolder.OnItemClickListener mOnItemClickListener;
    private PhotoViewHolder.OnItemLongClickListener mOnItemLongClickListener;

    public AlbumAdapter(Context context,
                        PhotoViewHolder.OnItemClickListener onItemClickListener,
                        PhotoViewHolder.OnItemLongClickListener onItemLongClickListener) {
        mContext = context;
        mOnItemClickListener = onItemClickListener;
        mOnItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_album, null, false);
        return new PhotoViewHolder(view, mOnItemClickListener, mOnItemLongClickListener);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        PhotoNote photoNote = mPhotoNoteList.get(position);
        if (photoNote.isSelected()) {
            holder.checkLayout.setVisibility(View.VISIBLE);
        } else {
            holder.checkLayout.setVisibility(View.INVISIBLE);
        }
//        ImageLoaderManager.displayImage(photoNote.getSmallPhotoPathWithFile(), holder.imageView);
        ImageLoaderManager.displayImage("http://pic31.nipic.com/20130801/11604791_100539834000_2.jpg", holder.imageView);
        int color = photoNote.getPaletteColor();
        PalmApp.setBackgroundDrawable(holder.checkLayout,
                new ColorDrawable(Color.argb(0x70, Color.red(color), Color.green(color), Color.blue(color))));//todo 优化
    }

    @Override
    public int getItemCount() {
        return mPhotoNoteList == null ? 0 : mPhotoNoteList.size();
    }

    /**
     * 点item的菜单时候的删除
     *
     * @param selected
     * @param position
     */
    public void setSelectedPosition(boolean selected, int position) {
        mPhotoNoteList.get(position).setSelected(selected);
        notifyItemChanged(position);
    }

    /**
     * 全选
     */
    public void selectAllPhotos() {
        for (int i = 0; i < mPhotoNoteList.size(); i++) {
            mPhotoNoteList.get(i).setSelected(true);
            notifyItemChanged(i);
        }
    }

    /**
     * 取消选择所有照片
     */
    public void cancelSelectPhotos() {
        for (PhotoNote photoNote : mPhotoNoteList) {
            photoNote.setSelected(false);
            int index = mPhotoNoteList.indexOf(photoNote);
            notifyItemChanged(index);
        }
    }

    /**
     * 照片是否被选择了
     *
     * @param position
     * @return
     */
    public boolean isPhotoSelected(int position) {
        return mPhotoNoteList.get(position).isSelected();
    }

    /**
     * 更新数据
     *
     * @param photoNotes
     */
    public void updateData(List<PhotoNote> photoNotes) {
        mPhotoNoteList = photoNotes;
        notifyDataSetChanged();
    }

    public void updateDataNoChange(List<PhotoNote> photoNotes) {
        mPhotoNoteList = photoNotes;
    }
}
