/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.forms.editor.client.editor.events;

import org.kie.workbench.common.forms.editor.client.editor.FormEditorHelper;

public class FormEditorContextResponse extends FormEditorEvent {
    protected FormEditorHelper editorHelper;

    public FormEditorContextResponse( String formId, String fieldId, FormEditorHelper formEditorHelper ) {
        super(formId, fieldId);
        this.editorHelper = formEditorHelper;
    }

    public FormEditorHelper getEditorHelper() {
        return editorHelper;
    }

    public void setEditorHelper(FormEditorHelper editorHelper) {
        this.editorHelper = editorHelper;
    }
}