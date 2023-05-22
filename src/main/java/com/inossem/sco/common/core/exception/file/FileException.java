package com.inossem.sco.common.core.exception.file;

import com.inossem.sco.common.core.exception.base.BaseException;

public class FileException extends BaseException {
    private static final long serialVersionUID = 1L;

    public FileException(String code, Object[] args) {
        super("file", code, args, (String)null);
    }
}
