package dev.ime.application.utils;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import dev.ime.common.constants.GlobalConstants;
import reactor.core.publisher.Mono;

@Component
public class LogHelper {

	public <T> Mono<T> addFinalLog(Mono<T> reactiveFlow, PrintHelper printHelper, Logger logger) {

		return reactiveFlow
				.doOnSubscribe(subscribed -> logInfo(GlobalConstants.MSG_FLOW_SUBS, subscribed.toString(), printHelper,
						logger))
				.doOnSuccess(
						success -> logInfo(GlobalConstants.MSG_FLOW_OK, createExtraInfo(success), printHelper, logger))
				.doOnCancel(
						() -> logInfo(GlobalConstants.MSG_FLOW_CANCEL, GlobalConstants.MSG_NODATA, printHelper, logger))
				.doOnError(error -> logInfo(GlobalConstants.MSG_FLOW_ERROR, error.toString(), printHelper, logger))
				.doFinally(signal -> logInfo(GlobalConstants.MSG_FLOW_RESULT, signal.toString(), printHelper, logger));
	}

	private void logInfo(String action, String extraInfo, PrintHelper printHelper, Logger logger) {

		printHelper.printResult(action, extraInfo, logger);
	}

	private <T> String createExtraInfo(T response) {

		return response instanceof Number ? GlobalConstants.MSG_MODLINES + response.toString() : response.toString();
	}

	public <T> void logFlowStep(T data, PrintHelper printHelper, Logger logger) {

		printHelper.printResult(GlobalConstants.MSG_FLOW_PROCESS, data != null ? data.toString() : "", logger);
	}
}
