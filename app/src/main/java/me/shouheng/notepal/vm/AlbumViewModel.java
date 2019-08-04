package me.shouheng.notepal.vm;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.shouheng.commons.model.data.Resource;
import me.shouheng.notepal.adapter.bean.SoundBean;
import me.shouheng.notepal.fragment.album.entity.PhotoNote;

/**
 * 相片列表
 *
 * @author dongyang_wu
 * @date 2019/7/24 20:18
 */
public class AlbumViewModel extends ViewModel {
    private MutableLiveData<Resource<List<PhotoNote>>> photoNotes;
    private MutableLiveData<Resource<List<SoundBean>>> soundDatas;

    public MutableLiveData<Resource<List<PhotoNote>>> getPhotoNotes() {
        if (photoNotes == null) {
            photoNotes = new MutableLiveData<>();
        }
        return photoNotes;
    }

    public MutableLiveData<Resource<List<SoundBean>>> getSoundDatas() {
        if (soundDatas == null) {
            soundDatas = new MutableLiveData<>();
        }
        return soundDatas;
    }

    public Disposable getPhotoData(String filePath) {
        return Observable
                .create((ObservableOnSubscribe<List<PhotoNote>>) emitter -> {
                    List<PhotoNote> items = new ArrayList<>();
                    String[] fileNames = filePath.split(" ");
                    for (String s : fileNames)
                        items.add(PhotoNote.builder().createdPhotoTime(123L).photoName(s).isSelected(false).build());
                    for (String s : fileNames)
                        items.add(PhotoNote.builder().createdPhotoTime(124L).photoName(s).isSelected(false).build());
                    for (String s : fileNames)
                        items.add(PhotoNote.builder().createdPhotoTime(125L).photoName(s).isSelected(false).build());
                    emitter.onNext(items);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(items -> {
                    System.out.println("获取数据" + items);
                    if (photoNotes != null) {
                        photoNotes.setValue(Resource.success(items));
                    }
                });
    }

    public Disposable getSoundData(String filePath) {
        return Observable
                .create((ObservableOnSubscribe<List<SoundBean>>) emitter -> {
                    List<SoundBean> items = new ArrayList<>();
                    String[] fileNames = filePath.split(" ");
                    for (String s : fileNames)
                        items.add(SoundBean.builder().createTime(new Timestamp(System.currentTimeMillis())).name(s).path("声音路径").build());
                    for (String s : fileNames)
                        items.add(SoundBean.builder().createTime(new Timestamp(System.currentTimeMillis())).name(s).path("声音路径").build());
                    for (String s : fileNames)
                        items.add(SoundBean.builder().createTime(new Timestamp(System.currentTimeMillis())).name(s).path("声音路径").build());
                    emitter.onNext(items);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(items -> {
                    System.out.println("获取数据" + items);
                    if (soundDatas != null) {
                        soundDatas.setValue(Resource.success(items));
                    }
                });
    }
}
