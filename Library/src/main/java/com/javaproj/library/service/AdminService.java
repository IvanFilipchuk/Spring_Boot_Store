package com.javaproj.library.service;

import com.javaproj.library.dto.AdminDto;
import com.javaproj.library.model.Admin;

public interface AdminService {
    Admin save(AdminDto adminDto);

    Admin findByUsername(String username);
}
