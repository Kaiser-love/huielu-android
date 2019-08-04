package me.shouheng.notepal.data.network.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dongyang_wu
 * @date 2019/7/26 19:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult<T> {
    private T data;
    private Integer code;
    private String msg;

    public boolean isSuccess() {
        return code == 1;
    }

}
