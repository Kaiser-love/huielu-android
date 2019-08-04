package me.shouheng.notepal.adapter;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.bean.SoundBean;

/**
 * @author dongyang_wu
 * @date 2019/7/26 09:20
 */
public class SoundDragAdapter extends BaseItemDraggableAdapter<SoundBean, BaseViewHolder> {
    public SoundDragAdapter(List data) {
        super(R.layout.item_sound_draggable, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SoundBean item) {
        switch (helper.getLayoutPosition() % 3) {
            case 0:
                helper.setImageResource(R.id.iv_head, R.mipmap.head_img0);
                break;
            case 1:
                helper.setImageResource(R.id.iv_head, R.mipmap.head_img1);
                break;
            case 2:
                helper.setImageResource(R.id.iv_head, R.mipmap.head_img2);
                break;
            default:
                break;
        }
        helper.setText(R.id.tv, item.getName());
    }
}