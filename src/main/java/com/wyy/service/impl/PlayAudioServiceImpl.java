package com.wyy.service.impl;


import com.wyy.enumer.AccountAuth;
import com.wyy.mapper.FolderMapper;
import com.wyy.mapper.NodeMapper;
import com.wyy.pojo.AudioInfoList;
import com.wyy.pojo.Node;
import com.wyy.service.PlayAudioService;
import com.wyy.util.AudioInfoUtil;
import com.wyy.util.ConfigureReader;
import com.wyy.util.FolderUtil;
import org.springframework.stereotype.*;

import com.google.gson.Gson;
import javax.annotation.*;
import javax.servlet.http.*;
import java.util.List;

@Service
public class PlayAudioServiceImpl implements PlayAudioService {
	@Resource
	private NodeMapper fm;
	@Resource
	private AudioInfoUtil aiu;
	@Resource
	private Gson gson;
	@Resource
	private FolderUtil fu;
	@Resource
	private FolderMapper flm;

	private AudioInfoList foundAudios(final HttpServletRequest request) {
		final String fileId = request.getParameter("fileId");
		if (fileId != null && fileId.length() > 0) {
			Node targetNode = fm.queryById(fileId);
			if (targetNode != null) {
				final String account = (String) request.getSession().getAttribute("ACCOUNT");
				if (ConfigureReader.instance().authorized(account, AccountAuth.DOWNLOAD_FILES,
						fu.getAllFoldersId(targetNode.getFileParentFolder()))
						&& ConfigureReader.instance().accessFolder(flm.queryById(targetNode.getFileParentFolder()),
								account)) {
					final List<Node> blocks = (List<Node>) this.fm.queryBySomeFolder(fileId);
					return this.aiu.transformToAudioInfoList(blocks, fileId);
				}
			}
		}
		return null;
	}

	/**
	 * <h2>解析播放音频文件</h2>
	 * <p>
	 * 根据音频文件的ID查询音频文件节点，以及同级目录下所有音频文件组成播放列表，并返回节点JSON信息，以便发起播放请求。
	 * </p>
	 * 
	 * @author kohgylw
	 * @param request
	 *            javax.servlet.http.HttpServletRequest 请求对象
	 * @return String 视频节点的JSON字符串
	 */
	public String getAudioInfoListByJson(final HttpServletRequest request) {
		final AudioInfoList ail = this.foundAudios(request);
		if (ail != null) {
			return gson.toJson((Object) ail);
		}
		return "ERROR";
	}
}
