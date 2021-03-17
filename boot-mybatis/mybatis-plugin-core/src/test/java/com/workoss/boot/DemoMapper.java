package com.workoss.boot;

import java.util.List;

public interface DemoMapper {

	DemoModel toTarget(DemoEntity source);

	List<DemoModel> toTargetList(List<DemoEntity> sourceList);
}
