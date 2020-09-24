package com.veer.filepicker.adapter;

/**
 * <li>Author:Veer</li>
 * <li>Date:  2020/3/6</li>
 * <li>Description: </li>
 */

public interface OnSelectStateListener<T> {
    void OnSelectStateChanged(boolean state, T file);
}
