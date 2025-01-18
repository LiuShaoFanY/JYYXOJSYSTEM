package com.schall.jyyxbackenduserservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schall.jyyx.model.entity.Announcement;
import com.schall.jyyxbackenduserservice.mapper.AnnouncementMapper;
import com.schall.jyyxbackenduserservice.service.AnnouncementService;
import com.schall.jyyxblackendcommon.common.ErrorCode;
import com.schall.jyyxblackendcommon.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    @Override
    public List<Announcement> getAllAnnouncements() {
        return this.list();
    }

    @Override
    public boolean createAnnouncement(Announcement announcement) {
        if (announcement == null || announcement.getTitle() == null || announcement.getContent() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "公告标题或内容为空");
        }
        announcement.setCreateTime(new Date());
        announcement.setUpdateTime(new Date());
        return this.save(announcement);
    }

    @Override
    public boolean updateAnnouncement(Announcement announcement) {
        if (announcement == null || announcement.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "公告ID为空");
        }
        announcement.setUpdateTime(new Date());
        return this.updateById(announcement);
    }

    @Override
    public boolean deleteAnnouncement(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "公告ID为空");
        }
        return this.removeById(id);
    }
}