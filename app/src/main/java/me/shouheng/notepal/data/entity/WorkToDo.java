package me.shouheng.notepal.data.entity;

import org.litepal.crud.LitePalSupport;

import java.sql.Timestamp;

import lombok.Builder;
import lombok.Data;

/**
 * 待处理任务
 *
 * @author dongyang_wu
 * @date 2019/7/24 10:40
 */
@Data
@Builder
public class WorkToDo extends LitePalSupport {
    private Timestamp createTime;
    private String photoFilePaths;
    private String soundFilePaths;
}
