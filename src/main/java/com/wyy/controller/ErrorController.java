package com.wyy.controller;

import javax.annotation.*;
import com.wyy.util.FileBlockUtil;
import com.wyy.util.LogUtil;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class ErrorController {
	@Resource
	private FileBlockUtil fbu;
	@Resource
	private LogUtil lu;

	@ExceptionHandler({ Exception.class })
	public void handleException(final Exception e) {
		this.lu.writeException(e);
		this.fbu.checkFileBlocks();
		System.out.println("\u5904\u7406\u8bf7\u6c42\u65f6\u53d1\u751f\u9519\u8bef\uff1a\n\r------\u4fe1\u606f------\n\r"
						+ e.getMessage() + "\n\r------\u4fe1\u606f------");
	}
}
