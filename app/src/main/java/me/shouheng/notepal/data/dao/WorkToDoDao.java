package me.shouheng.notepal.data.dao;

import org.litepal.LitePal;

import java.util.List;

import me.shouheng.notepal.data.entity.WorkToDo;

/**
 * 待处理任务数据接口层
 *
 * @author dongyang_wu
 * @date 2019/7/24 10:53
 */
public class WorkToDoDao {
    public static List<WorkToDo> findAll() {
        return LitePal.findAll(WorkToDo.class);
    }

}
