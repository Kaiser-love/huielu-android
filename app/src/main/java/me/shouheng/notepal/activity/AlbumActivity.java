package me.shouheng.notepal.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.previewlibrary.GPreviewBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.shouheng.commons.activity.CommonActivity;
import me.shouheng.commons.event.PageName;
import me.shouheng.commons.event.RxMessage;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.utils.ToastUtils;
import me.shouheng.commons.widget.recycler.DividerItemDecoration;
import me.shouheng.notepal.R;
import me.shouheng.notepal.adapter.AlbumAdapter;
import me.shouheng.notepal.adapter.SoundAdapter;
import me.shouheng.notepal.adapter.SoundDragAdapter;
import me.shouheng.notepal.adapter.bean.SoundBean;
import me.shouheng.notepal.databinding.ActivityAlbumBinding;
import me.shouheng.notepal.expand.bean.WorkList;
import me.shouheng.notepal.image.UserViewInfo;
import me.shouheng.notepal.manager.FileManager;
import me.shouheng.notepal.vm.AlbumViewModel;

import static me.shouheng.commons.event.UMEvent.PAGE_ALBUM;

/**
 * @author dongyang_wu
 * @date 2019/7/23 20:44
 */
@PageName(name = PAGE_ALBUM)
public class AlbumActivity extends CommonActivity<ActivityAlbumBinding> implements BaseQuickAdapter.OnItemLongClickListener, BaseQuickAdapter.OnItemClickListener {
    private static final String TAG = AlbumActivity.class.getSimpleName();

    /* 是不是选择模式 */
    private boolean mIsMenuSelectMode = false;
    /* menu的item */
    private MenuItem mSortMenuItem;
    private MenuItem mTrashMenuItem;
    private MenuItem mAllSelectMenuItem;
    private MenuItem mSelectMenuItem;
    private MenuItem mNewCategoryMenuItem;
    private MenuItem mSettingMenuItem;
    private MenuItem mMoveMenuItem;
    private Menu mMainMenu;
    /* RecyclerView布局 */
    private GridLayoutManager mGridLayoutManager;
    private AlbumViewModel viewModel;
    private AlbumAdapter mAdapter;
    private SoundAdapter soundAdapter;
    private SoundDragAdapter soundDragAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<SoundBean> mDatas = new ArrayList<>();
    private List<UserViewInfo> mThumbViewInfoList = new ArrayList<>();

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_album;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        viewModel = getViewModel(AlbumViewModel.class);
        addSubscriptions();
        Intent arguments = getIntent();
        String itemData = arguments.getStringExtra("itemData");
        String name = arguments.getStringExtra("name");
        int itemType = Integer.valueOf(arguments.getStringExtra("itemType"));
        System.out.println("workToDo: " + itemData);
        for (int i = 0; i < 6; i++) {
            mThumbViewInfoList.add(new UserViewInfo("http://pic31.nipic.com/20130801/11604791_100539834000_2.jpg"));
        }
        configToolbar(name);
        switch (itemType) {
            case WorkList.ListBean.TYPE_PHOTO:
                viewModel.getPhotoData(itemData);
                configPhotoView();
                break;
            case WorkList.ListBean.TYPE_SOUND:
                viewModel.getSoundData(itemData);
                configSoundView();
                break;
        }
    }

    private void configSoundView() {
        mLayoutManager = new LinearLayoutManager(getContext());
        getBinding().rvAlbum.setLayoutManager(new LinearLayoutManager(getContext()));
        getBinding().rvAlbum.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL_LIST, isDarkTheme()));
//        getBinding().ivEmpty.setSubTitle(viewModel.getEmptySubTitle());
        getBinding().rvAlbum.setEmptyView(getBinding().ivEmpty);
        OnItemDragListener onItemDragListener = new OnItemDragListener() {
            @Override
            public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
                Log.d(TAG, "drag start");
                BaseViewHolder holder = ((BaseViewHolder) viewHolder);
            }

            @Override
            public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {
                Log.d(TAG, "move from: " + source.getAdapterPosition() + " to: " + target.getAdapterPosition());
            }

            @Override
            public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
                Log.d(TAG, "drag end");
                BaseViewHolder holder = ((BaseViewHolder) viewHolder);
            }
        };
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(20);
        paint.setColor(Color.BLACK);
        OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
            @Override
            public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
                Log.d(TAG, "view swiped start: " + pos);
                BaseViewHolder holder = ((BaseViewHolder) viewHolder);
//                holder.setTextColor(R.id.tv, Color.WHITE);
            }

            @Override
            public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
                Log.d(TAG, "View reset: " + pos);
                BaseViewHolder holder = ((BaseViewHolder) viewHolder);
//                holder.setTextColor(R.id.tv, Color.BLACK);
            }

            @Override
            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
                Log.d(TAG, "View Swiped: " + pos);
            }

            @Override
            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {
                canvas.drawColor(ContextCompat.getColor(AlbumActivity.this, R.color.color_light_blue));
                canvas.drawText("删除", 0, 40, paint);
            }
        };
        soundDragAdapter = new SoundDragAdapter(Collections.emptyList());
        ItemDragAndSwipeCallback mItemDragAndSwipeCallback = new ItemDragAndSwipeCallback(soundDragAdapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(mItemDragAndSwipeCallback);
        mItemTouchHelper.attachToRecyclerView(getBinding().rvAlbum);

        //mItemDragAndSwipeCallback.setDragMoveFlags(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN);
        mItemDragAndSwipeCallback.setDragMoveFlags(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN);
        mItemDragAndSwipeCallback.setSwipeMoveFlags(ItemTouchHelper.START | ItemTouchHelper.END);
        soundDragAdapter.enableSwipeItem();
        soundDragAdapter.setOnItemSwipeListener(onItemSwipeListener);
        soundDragAdapter.enableDragItem(mItemTouchHelper, R.id.iv, false);
        soundDragAdapter.setOnItemDragListener(onItemDragListener);
//        mRecyclerView.addItemDecoration(new GridItemDecoration(this ,R.drawable.list_divider));
        getBinding().rvAlbum.setAdapter(soundDragAdapter);
        //        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
//            @Override
//            public void onSimpleItemClick(final BaseQuickAdapter adapter, final View view, final int position) {
//                ToastUtils.showShortToast("点击了" + position);
//            }
//        });
        soundDragAdapter.setOnItemClickListener((adapter, view, position) -> {
            // /storage/emulated/0
            System.out.println(Environment.getExternalStorageDirectory());
            //  /storage/emulated/0/Record
            System.out.println(Environment.getExternalStoragePublicDirectory("Record"));
            FileManager.openFile(getContext(), new File(Environment.getExternalStoragePublicDirectory("Record"), "record_20190726_10_33_20.wav"));
            System.out.println("点击了" + adapter.getData().get(position));
        });
        soundDragAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            showDeleteDialog(soundDragAdapter, position);
            return false;
        });
//        soundAdapter = new SoundAdapter(this, mDatas);
//        soundAdapter.setOnDelListener(new SoundAdapter.onSwipeListener() {
//            @Override
//            public void onDel(int pos) {
//                if (pos >= 0 && pos < mDatas.size()) {
//                    Toast.makeText(getContext(), "删除:" + pos, Toast.LENGTH_SHORT).show();
//                    soundAdapter.notifyItemRemoved(pos);//推荐用这个
//                    //如果删除时，不使用mAdapter.notifyItemRemoved(pos)，则删除没有动画效果，
//                    //且如果想让侧滑菜单同时关闭，需要同时调用 ((SwipeMenuLayout) holder.itemView).quickClose();
//                    //mAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onTop(int pos) {
//                if (pos > 0 && pos < mDatas.size()) {
//                    SoundBean swipeBean = mDatas.get(pos);
//                    mDatas.remove(swipeBean);
//                    soundAdapter.notifyItemInserted(0);
//                    mDatas.add(0, swipeBean);
//                    soundAdapter.notifyItemRemoved(pos + 1);
//                    if (mLayoutManager.findFirstVisibleItemPosition() == 0) {
//                        getBinding().rvAlbum.scrollToPosition(0);
//                    }
//                    //notifyItemRangeChanged(0,holder.getAdapterPosition()+1);
//                }
//            }
//        });
//
//        //6 2016 10 21 add , 增加viewChache 的 get()方法，
//        // 可以用在：当点击外部空白处时，关闭正在展开的侧滑菜单。我个人觉得意义不大，
//        getBinding().rvAlbum.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    SwipeMenuLayout viewCache = SwipeMenuLayout.getViewCache();
//                    if (null != viewCache) {
//                        viewCache.smoothClose();
//                    }
//                }
//                return false;
//            }
//        });
//        getBinding().rvAlbum.setAdapter(soundAdapter);
    }

    private void configPhotoView() {
        mGridLayoutManager = new GridLayoutManager(getContext(), 3);
        getBinding().rvAlbum.setEmptyView(getBinding().ivEmpty);
        getBinding().rvAlbum.setLayoutManager(mGridLayoutManager);
        getBinding().rvAlbum.setItemAnimator(new DefaultItemAnimator());
        //屏幕宽度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        mAdapter = new AlbumAdapter(getContext(), Collections.emptyList(), screenWidth);
        mAdapter.setOnItemLongClickListener(this);
        mAdapter.setOnItemClickListener(this);
        getBinding().rvAlbum.setAdapter(mAdapter);
    }

    private void configToolbar(String name) {
        /* Config toolbar. */
        setSupportActionBar(getBinding().toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(name);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true); //设置返回键可用
            actionBar.setHomeAsUpIndicator(ColorUtils.tintDrawable(R.drawable.ic_arrow_back_black_24dp,
                    isDarkTheme() ? Color.WHITE : Color.BLACK));
        }
        getBinding().toolbar.setTitleTextColor(isDarkTheme() ? Color.WHITE : Color.BLACK);
        if (isDarkTheme()) {
            getBinding().toolbar.setPopupTheme(R.style.AppTheme_PopupOverlayDark);
        }
    }

    private void addSubscriptions() {
        viewModel.getPhotoNotes().observe(this, resources -> {
            assert resources != null;
            System.out.println("数据改变:" + resources);
            switch (resources.status) {
                case SUCCESS:
//                    mAdapter.setNewData(resources.data);
                    postEvent(new RxMessage(RxMessage.CODE_PHOTO_DATA_CHANGE, resources.data));
                    mAdapter.setNewData(resources.data);
                    System.out.println("成功获取图片数据---" + resources.data);
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed);
                    break;
                case LOADING:
                    break;
            }
        });
        viewModel.getSoundDatas().observe(this, resources -> {
            assert resources != null;
            System.out.println("数据改变:" + resources);
            switch (resources.status) {
                case SUCCESS:
                    soundDragAdapter.setNewData(resources.data);
//                    mDatas = resources.data;
//                    soundAdapter.setmDatas(mDatas);
//                    soundAdapter.notifyDataSetChanged();
                    System.out.println("成功获取数据声音数据----" + resources.data);
                    break;
                case FAILED:
                    ToastUtils.makeToast(R.string.text_failed);
                    break;
                case LOADING:
                    break;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_album, menu);
        boolean isNotMenuExist = mMainMenu == null;
        mMainMenu = menu;
        mSortMenuItem = menu.findItem(R.id.menu_sort);
        mTrashMenuItem = menu.findItem(R.id.menu_trash);
        mAllSelectMenuItem = menu.findItem(R.id.menu_all_select);
        mSelectMenuItem = menu.findItem(R.id.menu_select);
        mNewCategoryMenuItem = menu.findItem(R.id.menu_new_file);
        mSettingMenuItem = menu.findItem(R.id.menu_setting);
        mMoveMenuItem = menu.findItem(R.id.menu_move);
        setAlbumSortKind(menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * menu为选择模式
     */
    private void menuSelectMode() {
        mSettingMenuItem.setVisible(false);
        mSortMenuItem.setVisible(false);
        mSelectMenuItem.setVisible(false);
        mNewCategoryMenuItem.setVisible(false);
        mTrashMenuItem.setVisible(true);
        mAllSelectMenuItem.setVisible(true);
        mMoveMenuItem.setVisible(true);
        mIsMenuSelectMode = true;
    }

    /**
     * menu为显示模式
     */
    private void menuPreviewMode() {
        mSettingMenuItem.setVisible(true);
        mSortMenuItem.setVisible(true);
        mSelectMenuItem.setVisible(true);
        mNewCategoryMenuItem.setVisible(true);
        mTrashMenuItem.setVisible(false);
        mAllSelectMenuItem.setVisible(false);
        mMoveMenuItem.setVisible(false);
        mIsMenuSelectMode = false;
    }

    /**
     * 在menu中设置排序方式
     *
     * @param menu
     */
    private void setAlbumSortKind(Menu menu) {
//        int sort = mAlbumPresenter.getAlbumSort();
        int sort = 3;
        switch (sort) {
            case 1:
                menu.findItem(R.id.menu_sort_create_far).setChecked(true);
                break;
            case 2:
                menu.findItem(R.id.menu_sort_create_close).setChecked(true);
                break;
            case 3:
                menu.findItem(R.id.menu_sort_edit_far).setChecked(true);
                break;
            case 4:
                menu.findItem(R.id.menu_sort_edit_close).setChecked(true);
                break;
        }
    }

    /**
     * 如果是再select模式的话，换为preview模式
     *
     * @return
     */
    public boolean isMenuSelectModeAndChangeIt() {
        if (mIsMenuSelectMode) {
            menuPreviewMode();
            mAdapter.cancelSelectPhotos();
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_sort_create_far:
                item.setChecked(true);
                break;
            case R.id.menu_sort_create_close:
                item.setChecked(true);
                break;
            case R.id.menu_sort_edit_far:
                item.setChecked(true);
                break;
            case R.id.menu_sort_edit_close:
//                mAlbumPresenter.setAlbumSort(4);
//                mAlbumPresenter.sortData();
                item.setChecked(true);
                break;
            case R.id.menu_trash:
//                mAlbumPresenter.deletePhotos();
                menuPreviewMode();
                break;
            case R.id.menu_all_select:
                mAdapter.selectAllPhotos();
                break;
            case R.id.menu_move:
//                mAlbumPresenter.movePhotos2AnotherCategory();
                break;
            case R.id.menu_setting:
                break;
            case R.id.menu_select:
                if (!mIsMenuSelectMode) {
                    menuSelectMode();
                } else {
                    menuPreviewMode();
                }
                break;
            case R.id.menu_new_file:
                System.out.println("新建分类");
                break;
        }
        return true;
    }

//    @Override
//    public void onItemClick(View v, int layoutPosition, int adapterPosition) {
//        if (mIsMenuSelectMode) {
//            if (!mAdapter.isPhotoSelected(adapterPosition)) {
//                mAdapter.setSelectedPosition(true, adapterPosition);
//            } else {
//                mAdapter.setSelectedPosition(false, adapterPosition);
//            }
//        }
//    }
//
//    @Override
//    public boolean onItemLongClick(View v, int layoutPosition, int adapterPosition) {
//        if (!mIsMenuSelectMode) {
//            menuSelectMode();
//        }
//        mAdapter.setSelectedPosition(true, adapterPosition);
//        return true;
//    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (mIsMenuSelectMode) {
            if (!mAdapter.isPhotoSelected(position)) {
                mAdapter.setSelectedPosition(true, position);
            } else {
                mAdapter.setSelectedPosition(false, position);
            }
        } else {
            // http://pic31.nipic.com/20130801/11604791_100539834000_2.jpg
            computeBoundsBackward(position);
            Object o = adapter.getData().get(position);
            System.out.println(o);
            //打开预览界面
            GPreviewBuilder.from(this)
                    .setData(mThumbViewInfoList)
                    .setCurrentIndex(position)
                    .setSingleFling(false)//是否在黑屏区域点击返回
                    .setDrag(false)//是否禁用图片拖拽返回
                    .setSingleFling(true)
                    .setType(GPreviewBuilder.IndicatorType.Number)
                    // 小圆点
//  .setType(GPreviewBuilder.IndicatorType.Dot)
                    .start();//启动
        }
    }

    /**
     * 查找信息
     * 从第一个完整可见item逆序遍历，如果初始位置为0，则不执行方法内循环
     */
    private void computeBoundsBackward(int firstCompletelyVisiblePos) {
        for (int i = firstCompletelyVisiblePos; i < mThumbViewInfoList.size(); i++) {
            View itemView = getBinding().rvAlbum.getChildAt(i - firstCompletelyVisiblePos);
            Rect bounds = new Rect();
            if (itemView != null) {
                //需要显示过度控件
//                ImageView thumbView = (ImageView) itemView.findViewById(R.id.iv);
//                //拿到在控件屏幕可见中控件显示为矩形Rect信息
//                thumbView.getGlobalVisibleRect(bounds);
            }
            mThumbViewInfoList.get(i).setBounds(bounds);
        }
    }

    @Override
    public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
        if (!mIsMenuSelectMode) {
            menuSelectMode();
        }
        mAdapter.setSelectedPosition(true, position);
        return true;
    }

    public class SingleItemPresenter {
        public void onItemClick(SoundBean data) {
            data.setName("修改之后立刻见效");
        }
    }

    private void showDeleteDialog(BaseQuickAdapter adapter, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle("提示!")
                .setMessage("您确定要删除此条录音")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.remove(position);
                        adapter.notifyDataSetChanged();
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
}
