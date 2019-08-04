package me.shouheng.notepal.vm;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.shouheng.commons.model.data.Resource;
import me.shouheng.notepal.data.dao.WorkToDoDao;
import me.shouheng.notepal.data.entity.WorkToDo;

/**
 * 待处理VM
 *
 * @author dongyang_wu
 * @date 2019/7/24 11:07
 */

public class WorkToDoViewModel extends ViewModel {

    private MutableLiveData<Resource<List<WorkToDo>>> workToData;

    public MutableLiveData<Resource<List<WorkToDo>>> getWorkToData() {
        if (workToData == null) {
            workToData = new MutableLiveData<>();
        }
        return workToData;
    }

    public Disposable getData() {
        return Observable
                .create((ObservableOnSubscribe<List<WorkToDo>>) emitter -> {
                    List<WorkToDo> workToDoList = WorkToDoDao.findAll();
                    emitter.onNext(workToDoList);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(notes -> {
                    System.out.println("获取数据" + notes);
                    System.out.println(Resource.success(notes));
                    if (workToData != null) {
                        workToData.setValue(Resource.success(notes));
                    }
                });
    }

}
