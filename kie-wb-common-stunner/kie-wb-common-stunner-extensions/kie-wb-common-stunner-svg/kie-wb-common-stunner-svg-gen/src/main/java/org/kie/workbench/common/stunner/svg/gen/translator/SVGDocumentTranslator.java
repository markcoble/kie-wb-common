/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.svg.gen.translator;

import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.gen.exception.TranslatorException;
import org.kie.workbench.common.stunner.svg.gen.model.ViewDefinition;
import org.w3c.dom.Document;

/**
 * Translates a given SVG document into an SVGViewDefinition instance
 */
public interface SVGDocumentTranslator extends Translator<Document> {

    String XLINK_URI = "http://www.w3.org/1999/xlink";
    String STUNNER_URI = "http://www.kie.org/2017/stunner";
    String STUNNER_ATTR_LAYOUT = "layout";
    String STUNNER_ATTR_SHAPE = "shape";
    String STUNNER_ATTR_SHAPE_MAIN = "main-shape";
    String STUNNER_ATTR_SHAPE_SCALABLE_GROUP = "scalable-group";

    ViewDefinition<SVGShapeView> translate(final Document input) throws TranslatorException;
}
