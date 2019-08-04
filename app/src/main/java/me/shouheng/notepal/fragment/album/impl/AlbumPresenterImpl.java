package me.shouheng.notepal.fragment.album.impl;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import javax.inject.Inject;

import me.shouheng.notepal.fragment.album.base.IView;
import me.shouheng.notepal.fragment.album.mvp.IAlbumPresenter;
import me.shouheng.notepal.fragment.album.mvp.IAlbumView;
import me.shouheng.notepal.fragment.album.rx.RxPhotoNote;
import rx.android.schedulers.AndroidSchedulers;

/**
 * @author dongyang_wu
 * @date 2019/7/23 14:51
 */
public class AlbumPresenterImpl implements IAlbumPresenter {
    private IAlbumView mAlbumView;

    private int mCategoryId = -1;
    private int mAlbumSortKind;

    private Context mContext;
    private RxPhotoNote mRxPhotoNote;


    @Inject
    public AlbumPresenterImpl(Context context, RxPhotoNote rxPhotoNote) {
        mContext = context;
        mRxPhotoNote = rxPhotoNote;
    }

    @Override
    public void bindData(int categoryId) {
        mCategoryId = categoryId;
    }

    @Override
    public void checkSandBox() {

    }

    @Override
    public void setAlbumSort(int sort) {

    }

    @Override
    public void saveAlbumSort() {

    }

    @Override
    public int getAlbumSort() {
        return 3;
    }

    @Override
    public void jump2DetailActivity(int position) {

    }

    @Override
    public void updateFromBroadcast(boolean broadcast_process, boolean broadcast_service, boolean broadcast_photo) {

    }

    @Override
    public void sortData() {
        mRxPhotoNote.findByCategoryId(mCategoryId, mAlbumSortKind)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(photoNoteList -> mAlbumView.notifyDataSetChanged(),
                        (throwable -> System.out.println(throwable)));
    }

    @Override
    public void changeCategoryWithPhotos(int categoryId) {

    }

    @Override
    public void movePhotos2AnotherCategory() {
        System.out.println("移动");
    }

    @Override
    public void changePhotosCategory(int categoryId) {

    }

    @Override
    public void deletePhotos() {

    }

    @Override
    public void createCategory(String newCategoryLabel) {

    }

    @Override
    public void savePhotoFromLocal(Uri imageUri) {

    }

    @Override
    public void savePhotoFromSystemCamera() {

    }

    @Override
    public void savePhotosFromGallery(ArrayList<String> list) {

    }

    @Override
    public void jump2Camera() {

    }

    @Override
    public boolean checkStorageEnough() {
        return false;
    }

    @Override
    public int calculateGridNumber() {
        return 0;
    }

    @Override
    public void attachView(@NonNull IView iView) {
        mAlbumView = (IAlbumView) iView;
        mRxPhotoNote.findByCategoryId(mCategoryId, mAlbumSortKind)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(photoNoteList -> mAlbumView.setAdapter(photoNoteList),
                        (throwable -> System.out.println(throwable)));
    }

    @Override
    public void detachView() {

    }

    @Override
    public IView getIView() {
        return mAlbumView;
    }
}
