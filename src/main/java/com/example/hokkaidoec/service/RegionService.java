package com.example.hokkaidoec.service;

import org.springframework.stereotype.Service;

import com.example.hokkaidoec.entity.Region;
import com.example.hokkaidoec.mapper.RegionMapper;

@Service
public class RegionService {

	private final RegionMapper regionMapper;

	public RegionService(RegionMapper regionMapper) {
		this.regionMapper = regionMapper;
	}

	public Region getRegionById(int regionId) {
		return regionMapper.findById(regionId);
	}
}
