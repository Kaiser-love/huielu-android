package me.shouheng.notepal.fragment.album.base;


import me.shouheng.notepal.aspect.permission.RequestType;

/**
 * Created by yuyidong on 15/11/13.
 */
public interface IView {

    /**
     * 权限用
     *
     * @return
     */
    IPresenter getPresenter();

    RequestType getRequestType();
}
