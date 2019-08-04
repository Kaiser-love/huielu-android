package me.shouheng.notepal.expand.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.dd.CircularProgressButton;
import com.jakewharton.rxbinding2.view.RxView;
import com.kongzue.dialog.listener.InputDialogOkButtonClickListener;
import com.kongzue.dialog.v2.InputDialog;
import com.kongzue.dialog.v2.SelectDialog;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import me.shouheng.notepal.R;
import me.shouheng.notepal.data.network.entity.FileUploadResult;
import me.shouheng.notepal.data.network.service.BaseObserver;
import me.shouheng.notepal.data.network.serviceImpl.FileService;
import me.shouheng.notepal.expand.bean.WorkList;
import me.shouheng.notepal.util.DialogUHelper;
import me.shouheng.notepal.util.ToastUtil;


/**
 * @author dongyang_wu
 * @date 2019/7/23 09:17
 */
public class WorkListAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public static final int TYPE_LEVEL_0 = 0;
    public static final int TYPE_LEVEL_1 = 1;
    private Context context;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public WorkListAdapter(Context context, List<MultiItemEntity> data) {
        super(data);
        this.context = context;
        addItemType(TYPE_LEVEL_0, R.layout.item_title);
        addItemType(TYPE_LEVEL_1, R.layout.item_message);
    }

    @NonNull
    @Override
    public List<MultiItemEntity> getData() {
        return super.getData();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void convert(final BaseViewHolder holder, final MultiItemEntity item) {
        switch (holder.getItemViewType()) {
            case TYPE_LEVEL_0:
                final WorkList workList = (WorkList) item;
                holder.setText(R.id.item_title1, workList.getTitle());
                holder.setImageResource(R.id.iv, workList.isExpanded() ? R.mipmap.arrow_b : R.mipmap.arrow_r);
                //添加该条目的点击事件
                holder.itemView.setOnClickListener(v -> {
                    int pos = holder.getAdapterPosition();
                    if (workList.isExpanded()) {
                        collapse(pos);
                        Toast.makeText(mContext, "收起：" + workList.getTitle(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        expand(pos);
                        Toast.makeText(mContext, "展开：" + workList.getTitle(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case TYPE_LEVEL_1:
                final WorkList.ListBean listBean = (WorkList.ListBean) item;
                boolean isPhoto = WorkList.ListBean.TYPE_PHOTO == listBean.getFileType();
                String text = isPhoto ? "会议照片" : "会议录音";
                holder.setText(R.id.item_message, text);
                final CircularProgressButton circularButton = holder.getView(R.id.item_button);
                circularButton.setIndeterminateProgressMode(true);
                if (isPhoto) {
                    RxView.clicks(circularButton)
                            .throttleFirst(1, TimeUnit.SECONDS)
                            .subscribe(new Consumer<Object>() {
                                @Override
                                public void accept(Object o) {
                                    if (circularButton.getProgress() > 0) {
                                        DialogUHelper.shopTips(context, circularButton, "正在下载中", 1000L);
                                        return;
                                    }
                                    final String[] items = new String[]{"生成PPT", "生成PDF"};
                                    System.out.println("文件路径:" + listBean.getFilePaths());
                                    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            InputDialog.show(context, "命名", "设置一个好听的名字吧", new InputDialogOkButtonClickListener() {
                                                @Override
                                                public void onClick(Dialog dialog, String inputText) {
                                                    String name = inputText;
                                                    circularButton.setProgress(50);
                                                    FileService.getInstance().uploadFilesToPpt(listBean.getFilePaths(), name, which, new BaseObserver<FileUploadResult>() {
                                                        @Override
                                                        protected void onSuccess(FileUploadResult t) throws Exception {
                                                            super.onSuccess(t);
                                                            circularButton.setProgress(100);
                                                            System.out.println(t);
                                                            ToastUtil.show("PPT创建成功" + t);
                                                            new Handler().postDelayed(() -> circularButton.setProgress(0), 1500);
                                                        }

                                                        @Override
                                                        public void onError(Throwable t) {
                                                            System.out.println(t);
                                                            circularButton.setProgress(-1);
                                                            ToastUtil.show("PPT创建失败" + t);
                                                            new Handler().postDelayed(() -> circularButton.setProgress(0), 1500);
                                                        }
                                                    });
                                                    dialog.dismiss();
                                                }
                                            });
                                            dialog.dismiss();
                                        }
                                    };
                                    DialogUHelper.shopSingleCheckableDialog(context, items, onClickListener);
                                }
                            });
                } else {
                    RxView.clicks(circularButton)
                            .throttleFirst(1, TimeUnit.SECONDS)
                            .subscribe(o -> SelectDialog.show(context, "提示", "确定上传到服务器处理吗？", (dialog, which) -> Toast.makeText(context, "您点击了确定按钮", Toast.LENGTH_SHORT).show()));
                }
                break;
        }
    }
}
