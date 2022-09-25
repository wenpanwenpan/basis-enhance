package org.basis.enhance.groovy.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * groovy文件过滤器
 *
 * @author wenpan 2022/09/18 16:58
 */
public class GroovyFileFilter implements FileFilter {

    @Override
    public boolean accept(File pathname) {
        return true;
    }
}
