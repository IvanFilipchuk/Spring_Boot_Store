package com.javaproj.library.service.impl;

import com.javaproj.library.model.Country;
import com.javaproj.library.repository.CountryRepository;
import com.javaproj.library.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;

    @Override
    public List<Country> findAll() {
        return countryRepository.findAll();
    }
}