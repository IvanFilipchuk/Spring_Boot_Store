package com.javaproj.library.service;

import com.javaproj.library.model.Country;

import java.util.List;

public interface CountryService {
    List<Country> findAll();
}