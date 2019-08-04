package me.shouheng.notepal.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.shouheng.commons.event.PageName;
import me.shouheng.commons.event.RxMessage;
import me.shouheng.commons.event.UMEvent;
import me.shouheng.commons.helper.ActivityHelper;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.commons.utils.ViewUtils;
import me.shouheng.data.entity.Category;
import me.shouheng.data.model.enums.Status;
import me.shouheng.notepal.PalmApp;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.AlbumActivity;
import me.shouheng.notepal.adapter.CategoriesAdapter;
import me.shouheng.notepal.data.entity.WorkToDo;
import me.shouheng.notepal.databinding.FragmentWorkToBinding;
import me.shouheng.notepal.dialog.CategoryEditDialog;
import me.shouheng.notepal.expand.adapter.WorkListAdapter;
import me.shouheng.notepal.expand.bean.WorkList;
import me.shouheng.notepal.vm.CategoriesViewModel;
import me.shouheng.notepal.vm.WorkToDoViewModel;

/**
 * Fragment used to display the categories.
 * <p>
 * Created by WngShhng (shouheng2015@gmail.com) on 2017/3/29.
 */
@PageName(name = UMEvent.PAGE_WORK_TO_DO)
public class WorkToDoFragment extends BaseFragment<FragmentWorkToBinding> implements BaseQuickAdapter.OnItemClickListener {

    /**
     * The argument key for this fragment. The status of current categories list.
     * Or null of showing the normal categories.
     */
    public final static String ARGS_KEY_STATUS = "__args_key_status";

    private RecyclerView.OnScrollListener scrollListener;
    private CategoriesAdapter mAdapter;
    private CategoriesViewModel viewModel;
    private ArrayList<MultiItemEntity> mList = new ArrayList<>();
    private WorkListAdapter mWorkListAdapter;
    private WorkToDoViewModel workToDoViewModel;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_work_to;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        workToDoViewModel = getViewModel(WorkToDoViewModel.class);
        configToolbar();
        workToDoViewModel.getData();
        configRecycleView();
        configRecycleListener();
//        mWorkListAdapter.expandAll();
//        mWorkListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
//            @Override
//            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//                Log.d("aaa", position + "");
//
//                switch (adapter.getItemViewType(position)) {
//                    case WorkListAdapter.TYPE_LEVEL_0:
//                        // TODO: 2019/4/16 关键代码，获取数据源（头部的）
//                        WorkList workList = (WorkList) mList.get(position);
//                        switch (view.getId()) {
//                            case R.id.item_title1:
//                                Toast.makeText(getContext(), workList.getTitle(),
//                                        Toast.LENGTH_SHORT).show();
//                                break;
//                            case R.id.arrow:
//                                Toast.makeText(getContext(), workList.getTitle(),
//                                        Toast.LENGTH_SHORT).show();
//                                break;
//                        }
//                        break;
//                    case WorkListAdapter.TYPE_LEVEL_1:
//                        // TODO: 2019/4/16 关键代码，获取数据源（子列表的）
//                        WorkList.ListBean listBean = (WorkList.ListBean) mList.get(position);
//                        switch (view.getId()) {
//                            case R.id.item_message1:
//                                System.out.println(listBean);
//                                Toast.makeText(getContext(), listBean.getFilePaths(),
//                                        Toast.LENGTH_SHORT).show();
//                                break;
//                        }
//
//                        break;
//                }
//            }
//        });
        addSubscriptions();
    }

    private void configRecycleView() {
        getBinding().rvWorkTo.setLayoutManager(new LinearLayoutManager(getContext()));
        mWorkListAdapter = new WorkListAdapter(getContext(), Collections.emptyList());
        mWorkListAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
        if (scrollListener != null) getBinding().rvWorkTo.addOnScrollListener(scrollListener);
//        DefaultItemAnimator animator = new DefaultItemAnimator();
//        animator.setAddDuration(300);
//        animator.setRemoveDuration(300);
//        getBinding().rvWorkTo.setItemAnimator(animator);
        View emptyView = View.inflate(getContext(), R.layout.empty_view, null);
        mWorkListAdapter.setEmptyView(emptyView);
        getBinding().rvWorkTo.setAdapter(mWorkListAdapter);
    }

    private void configRecycleListener() {
        // 头部点击下拉
        mWorkListAdapter.setOnItemClickListener((adapter, view, position) -> {
            switch (adapter.getItemViewType(position)) {
                case WorkListAdapter.TYPE_LEVEL_1:
                    WorkList.ListBean listBean = (WorkList.ListBean) mList.get(position);
                    int itemType = WorkList.ListBean.TYPE_PHOTO;
                    if (listBean.getFileType() == WorkList.ListBean.TYPE_SOUND) {
                        itemType = WorkList.ListBean.TYPE_SOUND;
                    }
                    WorkToDo build = WorkToDo.builder().createTime(new Timestamp(System.currentTimeMillis())).photoFilePaths("照片路径 照片路径").soundFilePaths("录音路径").build();
                    ActivityHelper.open(AlbumActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            .put("name", build.getCreateTime().toString())
                            .put("itemData", build.getPhotoFilePaths())
                            .put("itemType", itemType + "")
                            .launch(getActivity());
                    System.out.println("列表" + listBean);
                    break;
                default:
                    break;
            }
        });
        // 头部和内容长按删除
        mWorkListAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            showDeleteDialog(adapter, position, true);
            return false;
        });
        mWorkListAdapter.setOnItemChildLongClickListener((adapter, view, position) -> {
            showDeleteDialog(adapter, position, true);
            return false;
        });
    }

    private void showDeleteDialog(BaseQuickAdapter adapter, final int position, final boolean isGroup) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("提示!")
                .setMessage(isGroup ? "您确定要删除此组数据" : "您确定要删除此条数据")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.remove(position);
                        mWorkListAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private void initData(List<WorkToDo> data) {
        for (WorkToDo item : data) {
            WorkList workListItem = new WorkList();
            workListItem.setTitle(item.getCreateTime().toString());
            List<WorkList.ListBean> list = new ArrayList<>();
            WorkList.ListBean listBean = new WorkList.ListBean();
            listBean.setFileType(WorkList.ListBean.TYPE_PHOTO);
            listBean.setFilePaths(item.getPhotoFilePaths());
            list.add(listBean);
            listBean = new WorkList.ListBean();
            listBean.setFileType(WorkList.ListBean.TYPE_SOUND);
            listBean.setFilePaths(item.getSoundFilePaths());
            list.add(listBean);
            workListItem.setList(list);
            workListItem.setSubItems(list);
            mList.add(workListItem);
        }
    }

    private void configToolbar() {
        Activity activity = getActivity();
        if (activity != null) {
            ActionBar ab = ((AppCompatActivity) activity).getSupportActionBar();
            if (ab != null) {
                ab.setTitle(R.string.drawer_menu_work_to_do);
                ab.setSubtitle(null);
                ab.setDisplayHomeAsUpEnabled(true);
                ab.setHomeAsUpIndicator(ColorUtils.tintDrawable(R.drawable.ic_menu_black,
                        getThemeStyle().isDarkTheme ? Color.WHITE : Color.BLACK));
            }
        }
    }

    private void addSubscriptions() {
        addSubscription(RxMessage.class, RxMessage.CODE_PHOTO_DATA_CHANGE, rxMessage -> System.out.println(rxMessage.getObject()));
        workToDoViewModel.getWorkToData().observe(this, resources -> {
            assert resources != null;
            System.out.println("数据改变:" + resources);
            switch (resources.status) {
                case SUCCESS:
//                    mAdapter.setNewData(resources.data);
                    System.out.println("成功获取数据" + resources.data);
                    initData(resources.data);
                    mWorkListAdapter.setNewData(mList);
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed);
                    break;
                case LOADING:
                    break;
            }
        });
    }

    public void addCategory(Category category) {
        mAdapter.addData(0, category);
        getBinding().rvWorkTo.smoothScrollToPosition(0);
    }

    public void setScrollListener(RecyclerView.OnScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    private void popMenu(View v, Category param) {
        PopupMenu popupM = new PopupMenu(getContext(), v);
        popupM.inflate(R.menu.category_pop_menu);
        popupM.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_edit:
                    CategoryEditDialog.newInstance(param,
                            category -> viewModel.updateCategory(category)
                    ).show(getChildFragmentManager(), "CATEGORY_EDIT_DIALOG");
                    break;
                case R.id.action_delete:
                    viewModel.updateCategory(param, Status.DELETED);
                    break;
            }
            return true;
        });
        popupM.show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.capture, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_capture:
                createScreenCapture(getBinding().rvWorkTo, ViewUtils.dp2Px(PalmApp.getContext(), 60));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Activity activity = getActivity();
        if (activity instanceof CategoriesInteraction) {
            ((CategoriesInteraction) activity).onCategorySelected(mAdapter.getItem(position));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Activity activity = getActivity();
        if (activity instanceof CategoriesInteraction) {
            ((CategoriesInteraction) activity).onResumeToCategory();
        }
        configToolbar();
    }

    public interface CategoriesInteraction {

        default void onResumeToCategory() {
        }

        default void onCategorySelected(Category category) {
        }
    }
}