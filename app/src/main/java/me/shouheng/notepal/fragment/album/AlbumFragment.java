package me.shouheng.notepal.fragment.album;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.shouheng.notepal.R;
import me.shouheng.notepal.fragment.album.adapter.AlbumAdapter;
import me.shouheng.notepal.aspect.permission.RequestType;
import me.shouheng.notepal.fragment.album.base.BaseFragment;
import me.shouheng.notepal.fragment.album.entity.Category;
import me.shouheng.notepal.fragment.album.entity.PhotoNote;
import me.shouheng.notepal.fragment.album.impl.AlbumPresenterImpl;
import me.shouheng.notepal.fragment.album.mvp.IAlbumView;
import me.shouheng.notepal.fragment.album.utils.Utils;
import me.shouheng.notepal.fragment.album.viewholder.PhotoViewHolder;
import me.shouheng.notepal.widget.RevealView;

/**
 * 相片列表
 *
 * @author dongyang_wu
 * @date 2019/7/23 14:12
 */
public class AlbumFragment extends BaseFragment implements IAlbumView, PhotoViewHolder.OnItemClickListener,
        PhotoViewHolder.OnItemLongClickListener {
    @Inject
    AlbumPresenterImpl mAlbumPresenter;
    /* RecyclerView布局 */
    private GridLayoutManager mGridLayoutManager;
    /* Handler */
    private Handler mMainHandler;
    /* RevealColor */
    private RevealView mLayoutRevealView;
    @BindView(R.id.reveal_album)
    RevealView mAlbumRevealView;
    @BindView(R.id.rv_album)
    RecyclerView mRecyclerView;
    /* RecyclerView的适配器 */
    private AlbumAdapter mAdapter;
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

    public static AlbumFragment newInstance() {
        return new AlbumFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void getBundle(Bundle bundle) {
        mAlbumPresenter.bindData(bundle.getInt("categoryId4PhotoNotes"));
        mMainHandler = new Handler();
    }

    @Override
    public View inflateView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.frag_album, null);
    }

    @Override
    public void initInjector() {
        mIPresenter = mAlbumPresenter;
    }

    @Override
    public void initUI(View view) {
        ButterKnife.bind(this, view);
        mGridLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAlbumPresenter.attachView(this);
        mLayoutRevealView = (RevealView) getActivity().findViewById(R.id.reveal_layout);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_album, menu);
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
        if (isNotMenuExist) {
            mAlbumPresenter.sortData();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_create_far:
                mAlbumPresenter.setAlbumSort(1);
                mAlbumPresenter.sortData();
                item.setChecked(true);
                break;
            case R.id.menu_sort_create_close:
                mAlbumPresenter.setAlbumSort(2);
                mAlbumPresenter.sortData();
                item.setChecked(true);
                break;
            case R.id.menu_sort_edit_far:
                mAlbumPresenter.setAlbumSort(3);
                mAlbumPresenter.sortData();
                item.setChecked(true);
                break;
            case R.id.menu_sort_edit_close:
                mAlbumPresenter.setAlbumSort(4);
                mAlbumPresenter.sortData();
                item.setChecked(true);
                break;
            case R.id.menu_trash:
                mAlbumPresenter.deletePhotos();
                menuPreviewMode();
                break;
            case R.id.menu_all_select:
                mAdapter.selectAllPhotos();
                break;
            case R.id.menu_move:
                mAlbumPresenter.movePhotos2AnotherCategory();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println(requestCode);
        System.out.println(resultCode);
        System.out.println(data);
    }

    /**
     * 在menu中设置排序方式
     *
     * @param menu
     */
    private void setAlbumSortKind(Menu menu) {
        int sort = mAlbumPresenter.getAlbumSort();
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

    @Override
    public void initData() {

    }

    @Override
    public void initListener(View view) {
    }

    @Override
    public void setAdapter(List<PhotoNote> photoNoteList) {
        int size = Utils.sScreenWidth / mAlbumPresenter.calculateGridNumber();
        mAdapter = new AlbumAdapter(getContext(), this, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void startSandBoxService() {

    }

    @Override
    public void jump2DetailActivity(int categoryId, int position, int comparator) {

    }

    @Override
    public void notifyDataSetChanged() {

    }

    @Override
    public void updateData(List<PhotoNote> photoNoteList) {

    }

    @Override
    public void updateDataNoChange(List<PhotoNote> photoNoteList) {

    }

    @Override
    public void showMovePhotos2AnotherCategoryDialog(String[] categoryIdArray, String[] categoryLabelArray) {

    }

    @Override
    public void notifyItemRemoved(int position) {

    }

    @Override
    public void notifyItemInserted(int position) {

    }

    @Override
    public void showToast(String message) {

    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }

    @Override
    public void changeActivityListMenuCategoryChecked(Category category) {

    }

    @Override
    public void jump2CameraActivity(int categoryId) {

    }

    @Override
    public void jump2CameraSystemActivity() {

    }

    @Override
    public void setToolBarTitle(String title) {

    }

    @Override
    public RequestType getRequestType() {
        return null;
    }

    @Override
    public void onItemClick(View v, int layoutPosition, int adapterPosition) {
        System.out.println("点击");
    }

    @Override
    public boolean onItemLongClick(View v, int layoutPosition, int adapterPosition) {
        System.out.println("长按");
        return false;
    }
}
