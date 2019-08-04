package me.shouheng.notepal.expand.bean;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import me.shouheng.notepal.expand.adapter.WorkListAdapter;


/**
 * WorkList
 *
 * @author dongyang_wu
 * @date 2019/7/24 16:58
 */
public class WorkList extends AbstractExpandableItem<WorkList.ListBean> implements MultiItemEntity {
    private String title;
    private List<WorkList.ListBean> list;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getItemType() {
        return WorkListAdapter.TYPE_LEVEL_0;
    }

    @Override
    public void setSubItems(List<WorkList.ListBean> list) {
        super.setSubItems(list);
    }


    public static class ListBean implements MultiItemEntity {
        /**
         * message1 : 我有一只小狗我有一只小狗我有一只小狗我有一只小狗1
         * message2 : 我有一只小狗我有一只小狗我有一只小狗我有一只小狗2
         */
        public static final int TYPE_PHOTO = 0;
        public static final int TYPE_SOUND = 1;
        private int fileType;
        private String filePaths;

        @Override
        public int getItemType() {
            return WorkListAdapter.TYPE_LEVEL_1;
        }

        public int getFileType() {
            return fileType;
        }

        public void setFileType(int fileType) {
            this.fileType = fileType;
        }

        public String getFilePaths() {
            return filePaths;
        }

        public void setFilePaths(String filePaths) {
            this.filePaths = filePaths;
        }
    }
}
