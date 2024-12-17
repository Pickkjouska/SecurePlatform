package org.example.secureplatform.service;

import org.example.secureplatform.common.ResponseResult;
import org.example.secureplatform.entity.files.DirInfo;
import org.example.secureplatform.entity.files.DirRequest;
import org.springframework.stereotype.Service;

@Service
public abstract class DirService {

    public abstract ResponseResult<DirInfo> SearchDir(DirRequest dirrequest);
}
