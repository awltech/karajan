package com.wordline.awltech.karajan.orchestrator;

import com.wordline.awltech.karajan.api.BatchData;

public interface Orchestrator {
	BatchData<?> getResult();
}
