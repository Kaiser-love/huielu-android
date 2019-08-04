package me.shouheng.notepal.data.network.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PPT implements Serializable {
    private static final long serialVersionUID = 1L;
    private String cover;
    private String datetime;
    private String fileId;
    private String name;
    private Long pptid;
    private String path;
    private Long userId;
}
