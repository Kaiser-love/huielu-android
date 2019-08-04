package me.shouheng.notepal.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.fragment.album.entity.PhotoNote;
import me.shouheng.notepal.fragment.album.utils.ImageManager.ImageLoaderManager;

/**
 * @author dongyang_wu
 * @date 2019/7/24 20:39
 */
public class AlbumAdapter extends BaseQuickAdapter<PhotoNote, BaseViewHolder> {
    private Context mContext;
    private Integer screenWidth;

    public AlbumAdapter(Context context, List<PhotoNote> data, Integer screenWidth) {
        super(R.layout.item_album, data);
        mContext = context;
        this.screenWidth = screenWidth;
    }

    @Override
    protected void convert(BaseViewHolder helper, PhotoNote item) {
        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) helper.getView(R.id.rl_item).getLayoutParams();
        params.width = screenWidth / 3;
        params.height = screenWidth / 3;
        if (item.isSelected()) {
            helper.setVisible(R.id.layout_item_album_check, true);
        } else {
            helper.setVisible(R.id.layout_item_album_check, false);
        }
//        ImageLoaderManager.displayImage(photoNote.getSmallPhotoPathWithFile(), holder.imageView);
        ImageLoaderManager.displayImage("http://pic31.nipic.com/20130801/11604791_100539834000_2.jpg", helper.getView(R.id.img_item_album));
        int color = item.getPaletteColor();
        PalmApp.setBackgroundDrawable(helper.getView(R.id.layout_item_album_check),
                new ColorDrawable(Color.argb(0x70, Color.red(color), Color.green(color), Color.blue(color))));//todo 优化
    }

    // 自定义方法

    /**
     * 点item的菜单时候的删除
     *
     * @param selected
     * @param position
     */
    public void setSelectedPosition(boolean selected, int position) {
        getData().get(position).setSelected(selected);
        notifyItemChanged(position);
    }

    /**
     * 全选
     */
    public void selectAllPhotos() {
        for (int i = 0; i < getData().size(); i++) {
            getData().get(i).setSelected(true);
            notifyItemChanged(i);
        }
    }

    /**
     * 取消选择所有照片
     */
    public void cancelSelectPhotos() {
        for (PhotoNote photoNote : getData()) {
            photoNote.setSelected(false);
            int index = getData().indexOf(photoNote);
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
        return getData().get(position).isSelected();
    }

}
