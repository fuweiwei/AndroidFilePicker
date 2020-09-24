package com.veer.filepicker.filter.callback;

import com.veer.filepicker.filter.entity.BaseFile;
import com.veer.filepicker.filter.entity.Directory;

import java.util.List;

/**
 * <li>Author:Veer</li>
 * <li>Date:  2020/3/6</li>
 * <li>Description: </li>
 */

public interface FilterResultCallback<T extends BaseFile> {
    void onResult(List<Directory<T>> directories);
}
