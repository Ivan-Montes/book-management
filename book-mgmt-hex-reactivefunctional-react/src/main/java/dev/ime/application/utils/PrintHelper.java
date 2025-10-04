package dev.ime.application.utils;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import dev.ime.common.constants.GlobalConstants;

@Component
public class PrintHelper {

	public void printResult(String action, boolean boolResult, Logger logger) {

		if (boolResult) {
			logger.info(GlobalConstants.MSG_PATTERN_INFO, action, boolResult);
		} else {
			logger.error(GlobalConstants.MSG_PATTERN_SEVERE, action, boolResult);
		}
	}

	public <T> void printResult(String action, T entity, Logger logger) {

		if (entity != null) {
			logger.info(GlobalConstants.MSG_PATTERN_INFO, action, entity);
		} else {
			logger.error(GlobalConstants.MSG_PATTERN_SEVERE, action, GlobalConstants.MSG_NODATA);
		}
	}
}
