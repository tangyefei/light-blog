package com.example.demo.japaflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.japaflow.entity.JfStudyTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface JfStudyTimeMapper extends BaseMapper<JfStudyTime> {

    @Update("UPDATE jf_study_time SET total_ms = total_ms + #{deltaMs}, last_active_at = #{activeAt}, updated_at = NOW() " +
            "WHERE user_id = #{userId} AND lesson_id = #{lessonId} AND module = #{module}")
    int incrementTotalMs(@Param("userId") Long userId,
                         @Param("lessonId") Integer lessonId,
                         @Param("module") String module,
                         @Param("deltaMs") Long deltaMs,
                         @Param("activeAt") LocalDateTime activeAt);
}
