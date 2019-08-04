package me.shouheng.notepal.data.network.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dongyang_wu
 * @date 2019/7/26 22:43
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadResult {
    private List errorResultList;
    private PPT object;
    private List successResultList;
}
