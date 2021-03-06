package ch.bailu.aat.activities;

import android.os.Bundle;

import ch.bailu.aat.services.editor.EditorHelper;


public class FileContentActivity extends AbsFileContentActivity{


    private static final String SOLID_KEY="file_content";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, SOLID_KEY);
    }

    @Override
    protected EditorHelper createEditorHelper() {
        return new EditorHelper(getServiceContext());
    }
}
