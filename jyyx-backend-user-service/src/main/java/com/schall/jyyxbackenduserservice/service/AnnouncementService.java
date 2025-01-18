package com.schall.jyyxbackenduserservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.schall.jyyx.model.entity.Announcement;

import java.util.List;

public interface AnnouncementService extends IService<Announcement> {
    List<Announcement> getAllAnnouncements();
    boolean createAnnouncement(Announcement announcement);
    boolean updateAnnouncement(Announcement announcement);
    boolean deleteAnnouncement(Long id);
}