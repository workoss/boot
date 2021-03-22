package com.workoss.boot;

import java.util.ArrayList;
import java.util.List;

public class DemoMapperImpl implements DemoMapper {

	@Override
	public DemoModel toTarget(DemoEntity source) {
		return new DemoModel();
	}

	@Override
	public List<DemoModel> toTargetList(List<DemoEntity> sourceList) {
		return new ArrayList<>();
	}

}
