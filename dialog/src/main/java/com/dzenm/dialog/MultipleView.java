package com.dzenm.dialog;

import android.view.View;

public class MultipleView implements MaterialDialog.IContentView {

    private DialogDelegate mD;

    /**
     * 选中的数据
     */
    private String[] mData;

    private boolean isLoop;

    @Override
    public View onCreateView(DialogDelegate delegate) {
        return null;
    }
}
