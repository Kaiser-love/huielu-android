package me.shouheng.notepal.data.entity;

import org.litepal.crud.LitePalSupport;

import java.sql.Timestamp;

import lombok.Builder;
import lombok.Data;

/**
 * 已完成任务
 *
 * @author dongyang_wu
 * @date 2019/7/24 10:49
 */
@Data
@Builder
public class WorkFinish extends LitePalSupport {
    private Timestamp createTime;
    private String photoFilePaths;
    private String soundFilePaths;
}
