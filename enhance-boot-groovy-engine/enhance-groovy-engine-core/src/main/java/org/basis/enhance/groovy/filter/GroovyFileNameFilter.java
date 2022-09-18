package org.basis.enhance.groovy.filter;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Filters only .groovy files
 *
 * @author wenpan 2022/9/18 3:02 下午
 */
public class GroovyFileNameFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String name) {
        // 过滤以.groovy结尾的文件
        return name.endsWith(".groovy");
    }
}