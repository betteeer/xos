package com.inossem.sco.common.core.utils.sql;

import com.inossem.sco.common.core.exception.UtilException;
import com.inossem.sco.common.core.utils.StringUtils;

public class SqlUtil {
    public static String SQL_REGEX = "select |insert |delete |update |drop |count |exec |chr |mid |master |truncate |char |and |declare ";
    public static String SQL_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";

    public SqlUtil() {
    }

    public static String escapeOrderBySql(String value) {
        if (StringUtils.isNotEmpty(value) && !isValidOrderBySql(value)) {
            throw new UtilException("参数不符合规范，不能进行查询");
        } else {
            return value;
        }
    }

    public static boolean isValidOrderBySql(String value) {
        return value.matches(SQL_PATTERN);
    }

    public static void filterKeyword(String value) {
        if (!StringUtils.isEmpty(value)) {
            String[] sqlKeywords = StringUtils.split(SQL_REGEX, "\\|");
            String[] var2 = sqlKeywords;
            int var3 = sqlKeywords.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                String sqlKeyword = var2[var4];
                if (StringUtils.indexOfIgnoreCase(value, sqlKeyword) > -1) {
                    throw new UtilException("参数存在SQL注入风险");
                }
            }

        }
    }
}
