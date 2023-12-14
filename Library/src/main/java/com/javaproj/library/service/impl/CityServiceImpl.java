package com.javaproj.library.service.impl;

import com.javaproj.library.model.City;
import com.javaproj.library.repository.CityRepository;
import com.javaproj.library.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;

    @Override
    public List<City> findAll() {
        return cityRepository.findAll();
    }
}