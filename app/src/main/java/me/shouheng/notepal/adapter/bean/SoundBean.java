package me.shouheng.notepal.adapter.bean;


import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dongyang_wu
 * @date 2019/7/25 20:40
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SoundBean {
    private Timestamp createTime;
    private String name;
    private String path;
}
